const express = require('express');
const sql = require('mssql');
const cors = require('cors');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

// SQL Server Express Bağlantı Ayarları
const dbConfig = {
    server: process.env.DB_SERVER || 'localhost\\SQLEXPRESS',
    database: process.env.DB_NAME || 'LibroQuestDB',
    user: process.env.DB_USER || 'sa',
    password: process.env.DB_PASSWORD || '',
    port: parseInt(process.env.DB_PORT) || 1433,
    options: {
        encrypt: false,
        trustServerCertificate: true,
        enableArithAbort: true
    },
    pool: {
        max: 10,
        min: 0,
        idleTimeoutMillis: 30000
    }
};

const JWT_SECRET = process.env.JWT_SECRET || 'libroquest_secret';

// Veritabanı bağlantı havuzu
let pool;
async function connectDB() {
    try {
        pool = await sql.connect(dbConfig);
        console.log('SQL Server Express bağlantısı başarılı!');
    } catch (err) {
        console.error('Veritabanı bağlantı hatası:', err.message);
        process.exit(1);
    }
}

// JWT Token doğrulama middleware
function authMiddleware(req, res, next) {
    const token = req.headers['authorization']?.split(' ')[1];
    if (!token) return res.status(401).json({ hata: 'Token gerekli' });
    try {
        const decoded = jwt.verify(token, JWT_SECRET);
        req.kullanici = decoded;
        next();
    } catch (err) {
        return res.status(403).json({ hata: 'Geçersiz token' });
    }
}

// Admin kontrolü middleware
function adminMiddleware(req, res, next) {
    if (req.kullanici.rol !== 'admin') {
        return res.status(403).json({ hata: 'Bu işlem için admin yetkisi gerekli' });
    }
    next();
}

// =============================================
// AUTH ENDPOINT'LERİ
// =============================================

// GİRİŞ YAP
app.post('/api/giris', async (req, res) => {
    try {
        const { kullaniciAdi, sifre } = req.body;
        const result = await pool.request()
            .input('kullaniciAdi', sql.NVarChar, kullaniciAdi)
            .query('SELECT * FROM Kullanicilar WHERE KullaniciAdi = @kullaniciAdi');

        if (result.recordset.length === 0) {
            return res.status(401).json({ hata: 'Kullanıcı bulunamadı' });
        }

        const kullanici = result.recordset[0];

        // Şifre kontrolü (plain text - basit demo için)
        if (kullanici.Sifre !== sifre) {
            return res.status(401).json({ hata: 'Şifre hatalı' });
        }

        const token = jwt.sign(
            { id: kullanici.KullaniciID, kullaniciAdi: kullanici.KullaniciAdi, rol: kullanici.Rol, ad: kullanici.Ad },
            JWT_SECRET,
            { expiresIn: '24h' }
        );

        res.json({
            mesaj: 'Giriş başarılı',
            token,
            kullanici: {
                id: kullanici.KullaniciID,
                ad: kullanici.Ad,
                soyad: kullanici.Soyad,
                kullaniciAdi: kullanici.KullaniciAdi,
                rol: kullanici.Rol,
                puan: kullanici.Puan,
                cinsiyet: kullanici.Cinsiyet,
                sinif: kullanici.Sinif
            }
        });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// KAYIT OL
app.post('/api/kayit', async (req, res) => {
    try {
        const { ad, soyad, cinsiyet, sinif, kullaniciAdi, sifre } = req.body;

        // Kullanıcı adı kontrolü
        const kontrol = await pool.request()
            .input('kullaniciAdi', sql.NVarChar, kullaniciAdi)
            .query('SELECT KullaniciID FROM Kullanicilar WHERE KullaniciAdi = @kullaniciAdi');

        if (kontrol.recordset.length > 0) {
            return res.status(400).json({ hata: 'Bu kullanıcı adı zaten kullanılıyor' });
        }

        const result = await pool.request()
            .input('ad', sql.NVarChar, ad)
            .input('soyad', sql.NVarChar, soyad)
            .input('cinsiyet', sql.NVarChar, cinsiyet)
            .input('sinif', sql.NVarChar, sinif)
            .input('kullaniciAdi', sql.NVarChar, kullaniciAdi)
            .input('sifre', sql.NVarChar, sifre)
            .query(`INSERT INTO Kullanicilar (Ad, Soyad, Cinsiyet, Sinif, KullaniciAdi, Sifre, Rol, Puan) 
                    OUTPUT INSERTED.KullaniciID
                    VALUES (@ad, @soyad, @cinsiyet, @sinif, @kullaniciAdi, @sifre, 'kullanici', 100)`);

        res.status(201).json({
            mesaj: 'Kayıt başarılı! 100 puan hesabınıza tanımlandı.',
            kullaniciId: result.recordset[0].KullaniciID
        });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// ŞİFREMİ UNUTTUM
app.post('/api/sifremi-unuttum', async (req, res) => {
    try {
        const { kullaniciAdi, yeniSifre } = req.body;
        const result = await pool.request()
            .input('kullaniciAdi', sql.NVarChar, kullaniciAdi)
            .input('yeniSifre', sql.NVarChar, yeniSifre)
            .query('UPDATE Kullanicilar SET Sifre = @yeniSifre WHERE KullaniciAdi = @kullaniciAdi');

        if (result.rowsAffected[0] === 0) {
            return res.status(404).json({ hata: 'Kullanıcı bulunamadı' });
        }
        res.json({ mesaj: 'Şifre başarıyla güncellendi' });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// =============================================
// KULLANICI ENDPOINT'LERİ
// =============================================

// Tüm kullanıcıları listele (admin)
app.get('/api/kullanicilar', authMiddleware, adminMiddleware, async (req, res) => {
    try {
        const result = await pool.request()
            .query('SELECT KullaniciID, Ad, Soyad, Cinsiyet, Sinif, KullaniciAdi, Rol, Puan, KayitTarihi FROM Kullanicilar ORDER BY Ad');
        res.json(result.recordset);
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// Tek kullanıcı getir
app.get('/api/kullanicilar/:id', authMiddleware, async (req, res) => {
    try {
        const result = await pool.request()
            .input('id', sql.Int, req.params.id)
            .query('SELECT KullaniciID, Ad, Soyad, Cinsiyet, Sinif, KullaniciAdi, Rol, Puan, KayitTarihi FROM Kullanicilar WHERE KullaniciID = @id');
        if (result.recordset.length === 0) {
            return res.status(404).json({ hata: 'Kullanıcı bulunamadı' });
        }
        res.json(result.recordset[0]);
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// Kullanıcı güncelle (admin)
app.put('/api/kullanicilar/:id', authMiddleware, adminMiddleware, async (req, res) => {
    try {
        const { ad, soyad, cinsiyet, sinif, puan, rol } = req.body;
        await pool.request()
            .input('id', sql.Int, req.params.id)
            .input('ad', sql.NVarChar, ad)
            .input('soyad', sql.NVarChar, soyad)
            .input('cinsiyet', sql.NVarChar, cinsiyet)
            .input('sinif', sql.NVarChar, sinif)
            .input('puan', sql.Int, puan)
            .input('rol', sql.NVarChar, rol)
            .query('UPDATE Kullanicilar SET Ad=@ad, Soyad=@soyad, Cinsiyet=@cinsiyet, Sinif=@sinif, Puan=@puan, Rol=@rol WHERE KullaniciID=@id');
        res.json({ mesaj: 'Kullanıcı güncellendi' });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// Kullanıcı sil (admin)
app.delete('/api/kullanicilar/:id', authMiddleware, adminMiddleware, async (req, res) => {
    try {
        await pool.request()
            .input('id', sql.Int, req.params.id)
            .query('DELETE FROM PuanGecmisi WHERE KullaniciID = @id');
        await pool.request()
            .input('id', sql.Int, req.params.id)
            .query('DELETE FROM OduncIslemleri WHERE KullaniciID = @id');
        await pool.request()
            .input('id', sql.Int, req.params.id)
            .query('DELETE FROM Kullanicilar WHERE KullaniciID = @id');
        res.json({ mesaj: 'Kullanıcı silindi' });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// =============================================
// YAZAR ENDPOINT'LERİ
// =============================================

// Tüm yazarları listele
app.get('/api/yazarlar', authMiddleware, async (req, res) => {
    try {
        const result = await pool.request()
            .query('SELECT * FROM Yazarlar ORDER BY Ad, Soyad');
        res.json(result.recordset);
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// Yazar ekle (admin)
app.post('/api/yazarlar', authMiddleware, adminMiddleware, async (req, res) => {
    try {
        const { ad, soyad } = req.body;
        const result = await pool.request()
            .input('ad', sql.NVarChar, ad)
            .input('soyad', sql.NVarChar, soyad)
            .query('INSERT INTO Yazarlar (Ad, Soyad) OUTPUT INSERTED.YazarID VALUES (@ad, @soyad)');
        res.status(201).json({ mesaj: 'Yazar eklendi', yazarId: result.recordset[0].YazarID });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// Yazar sil (admin)
app.delete('/api/yazarlar/:id', authMiddleware, adminMiddleware, async (req, res) => {
    try {
        await pool.request()
            .input('id', sql.Int, req.params.id)
            .query('DELETE FROM Yazarlar WHERE YazarID = @id');
        res.json({ mesaj: 'Yazar silindi' });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// =============================================
// KİTAP TÜRLERİ ENDPOINT'LERİ
// =============================================

// Tüm türleri listele
app.get('/api/turler', authMiddleware, async (req, res) => {
    try {
        const result = await pool.request()
            .query('SELECT * FROM KitapTurleri ORDER BY TurAdi');
        res.json(result.recordset);
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// Tür ekle (admin)
app.post('/api/turler', authMiddleware, adminMiddleware, async (req, res) => {
    try {
        const { turAdi } = req.body;
        const result = await pool.request()
            .input('turAdi', sql.NVarChar, turAdi)
            .query('INSERT INTO KitapTurleri (TurAdi) OUTPUT INSERTED.TurID VALUES (@turAdi)');
        res.status(201).json({ mesaj: 'Tür eklendi', turId: result.recordset[0].TurID });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// =============================================
// KİTAP ENDPOINT'LERİ
// =============================================

// Tüm kitapları listele (yazar ve tür bilgisiyle)
app.get('/api/kitaplar', authMiddleware, async (req, res) => {
    try {
        const result = await pool.request()
            .query(`SELECT k.*, 
                    y.Ad + ' ' + y.Soyad AS YazarAdSoyad,
                    t.TurAdi
                    FROM Kitaplar k
                    LEFT JOIN Yazarlar y ON k.YazarID = y.YazarID
                    LEFT JOIN KitapTurleri t ON k.TurID = t.TurID
                    ORDER BY k.KitapAdi`);
        res.json(result.recordset);
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// Tek kitap getir
app.get('/api/kitaplar/:id', authMiddleware, async (req, res) => {
    try {
        const result = await pool.request()
            .input('id', sql.Int, req.params.id)
            .query(`SELECT k.*, 
                    y.Ad + ' ' + y.Soyad AS YazarAdSoyad,
                    t.TurAdi
                    FROM Kitaplar k
                    LEFT JOIN Yazarlar y ON k.YazarID = y.YazarID
                    LEFT JOIN KitapTurleri t ON k.TurID = t.TurID
                    WHERE k.KitapID = @id`);
        if (result.recordset.length === 0) {
            return res.status(404).json({ hata: 'Kitap bulunamadı' });
        }
        res.json(result.recordset[0]);
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// Kitap ekle (admin)
app.post('/api/kitaplar', authMiddleware, adminMiddleware, async (req, res) => {
    try {
        const { kitapAdi, yazarId, turId, isbn, sayfaSayisi, stokAdedi } = req.body;
        const result = await pool.request()
            .input('kitapAdi', sql.NVarChar, kitapAdi)
            .input('yazarId', sql.Int, yazarId)
            .input('turId', sql.Int, turId)
            .input('isbn', sql.NVarChar, isbn)
            .input('sayfaSayisi', sql.Int, sayfaSayisi)
            .input('stokAdedi', sql.Int, stokAdedi || 1)
            .query(`INSERT INTO Kitaplar (KitapAdi, YazarID, TurID, ISBN, SayfaSayisi, StokAdedi) 
                    OUTPUT INSERTED.KitapID
                    VALUES (@kitapAdi, @yazarId, @turId, @isbn, @sayfaSayisi, @stokAdedi)`);
        res.status(201).json({ mesaj: 'Kitap eklendi', kitapId: result.recordset[0].KitapID });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// Kitap sil (admin)
app.delete('/api/kitaplar/:id', authMiddleware, adminMiddleware, async (req, res) => {
    try {
        await pool.request()
            .input('id', sql.Int, req.params.id)
            .query('DELETE FROM Kitaplar WHERE KitapID = @id');
        res.json({ mesaj: 'Kitap silindi' });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// =============================================
// ÖDÜNÇ İŞLEMLERİ ENDPOINT'LERİ
// =============================================

// Tüm ödünç işlemlerini listele
app.get('/api/odunc', authMiddleware, async (req, res) => {
    try {
        const result = await pool.request()
            .query(`SELECT o.*, 
                    k.Ad + ' ' + k.Soyad AS UyeAdSoyad,
                    kt.KitapAdi,
                    y.Ad + ' ' + y.Soyad AS YazarAdSoyad
                    FROM OduncIslemleri o
                    INNER JOIN Kullanicilar k ON o.KullaniciID = k.KullaniciID
                    INNER JOIN Kitaplar kt ON o.KitapID = kt.KitapID
                    LEFT JOIN Yazarlar y ON kt.YazarID = y.YazarID
                    ORDER BY o.OduncTarihi DESC`);
        res.json(result.recordset);
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// Kullanıcının ödünç aldığı kitaplar
app.get('/api/odunc/kullanici/:id', authMiddleware, async (req, res) => {
    try {
        const result = await pool.request()
            .input('id', sql.Int, req.params.id)
            .query(`SELECT o.*, 
                    kt.KitapAdi,
                    y.Ad + ' ' + y.Soyad AS YazarAdSoyad,
                    t.TurAdi,
                    CASE 
                        WHEN o.TeslimEdildiMi = 0 AND o.TeslimTarihi < GETDATE() 
                        THEN DATEDIFF(DAY, o.TeslimTarihi, GETDATE())
                        ELSE 0 
                    END AS GecikmeGunu,
                    CASE 
                        WHEN o.TeslimEdildiMi = 0 AND o.TeslimTarihi >= GETDATE() 
                        THEN DATEDIFF(DAY, GETDATE(), o.TeslimTarihi)
                        ELSE 0 
                    END AS KalanGun
                    FROM OduncIslemleri o
                    INNER JOIN Kitaplar kt ON o.KitapID = kt.KitapID
                    LEFT JOIN Yazarlar y ON kt.YazarID = y.YazarID
                    LEFT JOIN KitapTurleri t ON kt.TurID = t.TurID
                    WHERE o.KullaniciID = @id
                    ORDER BY o.TeslimEdildiMi ASC, o.TeslimTarihi ASC`);
        res.json(result.recordset);
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// Kitap ödünç ver (admin)
app.post('/api/odunc', authMiddleware, adminMiddleware, async (req, res) => {
    try {
        const { kullaniciId, kitapId, teslimTarihi } = req.body;

        // Stok kontrolü
        const stok = await pool.request()
            .input('kitapId', sql.Int, kitapId)
            .query(`SELECT StokAdedi - (SELECT COUNT(*) FROM OduncIslemleri WHERE KitapID = @kitapId AND TeslimEdildiMi = 0) AS MevcutStok 
                    FROM Kitaplar WHERE KitapID = @kitapId`);

        if (stok.recordset.length === 0) {
            return res.status(404).json({ hata: 'Kitap bulunamadı' });
        }
        if (stok.recordset[0].MevcutStok <= 0) {
            return res.status(400).json({ hata: 'Kitap stokta yok' });
        }

        const result = await pool.request()
            .input('kullaniciId', sql.Int, kullaniciId)
            .input('kitapId', sql.Int, kitapId)
            .input('teslimTarihi', sql.DateTime, new Date(teslimTarihi))
            .query(`INSERT INTO OduncIslemleri (KullaniciID, KitapID, TeslimTarihi) 
                    OUTPUT INSERTED.IslemID
                    VALUES (@kullaniciId, @kitapId, @teslimTarihi)`);

        res.status(201).json({ mesaj: 'Kitap ödünç verildi', islemId: result.recordset[0].IslemID });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// Kitap teslim al (admin)
app.put('/api/odunc/:id/teslim', authMiddleware, adminMiddleware, async (req, res) => {
    try {
        const islemId = req.params.id;

        // İşlem bilgisini al
        const islem = await pool.request()
            .input('islemId', sql.Int, islemId)
            .query('SELECT * FROM OduncIslemleri WHERE IslemID = @islemId');

        if (islem.recordset.length === 0) {
            return res.status(404).json({ hata: 'İşlem bulunamadı' });
        }

        const odunc = islem.recordset[0];
        const bugun = new Date();
        const teslimTarihi = new Date(odunc.TeslimTarihi);
        let puanDususu = 0;

        // Gecikme hesapla
        if (bugun > teslimTarihi) {
            const gecikmeGunu = Math.ceil((bugun - teslimTarihi) / (1000 * 60 * 60 * 24));
            if (gecikmeGunu >= 1 && gecikmeGunu <= 3) {
                puanDususu = gecikmeGunu * 5;
            } else if (gecikmeGunu >= 4 && gecikmeGunu <= 7) {
                puanDususu = 15 + (gecikmeGunu - 3) * 10;
            } else if (gecikmeGunu > 7) {
                puanDususu = 55 + (gecikmeGunu - 7) * 15;
            }
        }

        // İşlemi güncelle
        await pool.request()
            .input('islemId', sql.Int, islemId)
            .query('UPDATE OduncIslemleri SET TeslimEdildiMi = 1, GercekTeslimTarihi = GETDATE() WHERE IslemID = @islemId');

        // Puan düşür
        if (puanDususu > 0) {
            await pool.request()
                .input('kullaniciId', sql.Int, odunc.KullaniciID)
                .input('puanDususu', sql.Int, puanDususu)
                .query('UPDATE Kullanicilar SET Puan = Puan - @puanDususu WHERE KullaniciID = @kullaniciId');

            // Puan geçmişine ekle
            await pool.request()
                .input('kullaniciId', sql.Int, odunc.KullaniciID)
                .input('islemId', sql.Int, islemId)
                .input('puanDegisimi', sql.Int, -puanDususu)
                .input('aciklama', sql.NVarChar, `Gecikme cezası: ${puanDususu} puan düşürüldü`)
                .query('INSERT INTO PuanGecmisi (KullaniciID, IslemID, PuanDegisimi, Aciklama) VALUES (@kullaniciId, @islemId, @puanDegisimi, @aciklama)');
        }

        res.json({
            mesaj: 'Kitap teslim alındı',
            puanDususu,
            gecikmeVar: puanDususu > 0
        });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// =============================================
// GECİKEN KİTAPLAR ENDPOINT'İ
// =============================================

app.get('/api/gecikenler', authMiddleware, async (req, res) => {
    try {
        const result = await pool.request()
            .query(`SELECT 
                    o.IslemID,
                    k.KullaniciID,
                    k.Ad + ' ' + k.Soyad AS UyeAdSoyad,
                    k.Sinif,
                    k.Cinsiyet,
                    k.Puan,
                    kt.KitapAdi,
                    kt.KitapID,
                    y.Ad + ' ' + y.Soyad AS YazarAdSoyad,
                    o.OduncTarihi,
                    o.TeslimTarihi,
                    DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) AS GecikmeGunu,
                    CASE 
                        WHEN DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) BETWEEN 1 AND 3 
                            THEN DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) * 5
                        WHEN DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) BETWEEN 4 AND 7 
                            THEN 15 + ((DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) - 3) * 10)
                        WHEN DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) > 7 
                            THEN 55 + ((DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) - 7) * 15)
                        ELSE 0
                    END AS PuanDususu
                    FROM OduncIslemleri o
                    INNER JOIN Kullanicilar k ON o.KullaniciID = k.KullaniciID
                    INNER JOIN Kitaplar kt ON o.KitapID = kt.KitapID
                    LEFT JOIN Yazarlar y ON kt.YazarID = y.YazarID
                    WHERE o.TeslimEdildiMi = 0 AND o.TeslimTarihi < GETDATE()
                    ORDER BY DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) DESC`);
        res.json(result.recordset);
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// =============================================
// İSTATİSTİK ENDPOINT'İ (Admin Dashboard)
// =============================================
app.get('/api/istatistikler', authMiddleware, adminMiddleware, async (req, res) => {
    try {
        const toplamKitap = await pool.request().query('SELECT COUNT(*) AS toplam FROM Kitaplar');
        const toplamUye = await pool.request().query("SELECT COUNT(*) AS toplam FROM Kullanicilar WHERE Rol = 'kullanici'");
        const aktifOdunc = await pool.request().query('SELECT COUNT(*) AS toplam FROM OduncIslemleri WHERE TeslimEdildiMi = 0');
        const gecikenler = await pool.request().query('SELECT COUNT(*) AS toplam FROM OduncIslemleri WHERE TeslimEdildiMi = 0 AND TeslimTarihi < GETDATE()');

        res.json({
            toplamKitap: toplamKitap.recordset[0].toplam,
            toplamUye: toplamUye.recordset[0].toplam,
            aktifOdunc: aktifOdunc.recordset[0].toplam,
            gecikenSayisi: gecikenler.recordset[0].toplam
        });
    } catch (err) {
        res.status(500).json({ hata: err.message });
    }
});

// =============================================
// SUNUCU BAŞLAT
// =============================================
const PORT = process.env.PORT || 3000;

connectDB().then(() => {
    app.listen(PORT, '0.0.0.0', () => {
        console.log(`LibroQuest API sunucusu ${PORT} portunda çalışıyor`);
        console.log(`http://localhost:${PORT}`);
    });
});
