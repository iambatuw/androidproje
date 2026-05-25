const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');

const app = express();
app.use(cors());
app.use(express.json());

const JWT_SECRET = 'libroquest_secret_key_2024';

// =============================================
// IN-MEMORY VERİTABANI (Demo Amaçlı)
// =============================================

let nextKullaniciId = 5;
let nextYazarId = 6;
let nextTurId = 7;
let nextKitapId = 11;
let nextIslemId = 4;

const kullanicilar = [
    { KullaniciID: 1, Ad: 'Admin', Soyad: 'Yönetici', Cinsiyet: 'Erkek', Sinif: '-', KullaniciAdi: 'admin', Sifre: 'admin123', GuvenlikAnahtari: 'admin', Rol: 'admin', Puan: 100 },
    { KullaniciID: 2, Ad: 'Ayşe', Soyad: 'Yılmaz', Cinsiyet: 'Kadın', Sinif: '10-A', KullaniciAdi: 'ayse.yilmaz', Sifre: '123456', GuvenlikAnahtari: 'ayse', Rol: 'kullanici', Puan: 100 },
    { KullaniciID: 3, Ad: 'Kerem', Soyad: 'Demir', Cinsiyet: 'Erkek', Sinif: '11-B', KullaniciAdi: 'kerem.demir', Sifre: '123456', GuvenlikAnahtari: 'kerem', Rol: 'kullanici', Puan: 85 },
    { KullaniciID: 4, Ad: 'Zeynep', Soyad: 'Kara', Cinsiyet: 'Kadın', Sinif: '9-C', KullaniciAdi: 'zeynep.kara', Sifre: '123456', GuvenlikAnahtari: 'zeynep', Rol: 'kullanici', Puan: 95 }
];

const yazarlar = [
    { YazarID: 1, Ad: 'Orhan', Soyad: 'Pamuk' },
    { YazarID: 2, Ad: 'Elif', Soyad: 'Şafak' },
    { YazarID: 3, Ad: 'Sabahattin', Soyad: 'Ali' },
    { YazarID: 4, Ad: 'Yaşar', Soyad: 'Kemal' },
    { YazarID: 5, Ad: 'Halide Edip', Soyad: 'Adıvar' }
];

const turler = [
    { TurID: 1, TurAdi: 'Roman' },
    { TurID: 2, TurAdi: 'Hikaye' },
    { TurID: 3, TurAdi: 'Şiir' },
    { TurID: 4, TurAdi: 'Bilim Kurgu' },
    { TurID: 5, TurAdi: 'Tarih' },
    { TurID: 6, TurAdi: 'Felsefe' }
];

const kitaplar = [
    { KitapID: 1, KitapAdi: 'Kar', YazarID: 1, TurID: 1, ISBN: '978-975-10-0001-1', SayfaSayisi: 436, StokAdedi: 3 },
    { KitapID: 2, KitapAdi: 'Benim Adım Kırmızı', YazarID: 1, TurID: 1, ISBN: '978-975-10-0002-2', SayfaSayisi: 472, StokAdedi: 2 },
    { KitapID: 3, KitapAdi: 'İstanbul Hatırası', YazarID: 2, TurID: 1, ISBN: '978-975-10-0003-3', SayfaSayisi: 320, StokAdedi: 4 },
    { KitapID: 4, KitapAdi: 'Aşk', YazarID: 2, TurID: 1, ISBN: '978-975-10-0004-4', SayfaSayisi: 368, StokAdedi: 5 },
    { KitapID: 5, KitapAdi: 'Kürk Mantolu Madonna', YazarID: 3, TurID: 1, ISBN: '978-975-10-0005-5', SayfaSayisi: 160, StokAdedi: 6 },
    { KitapID: 6, KitapAdi: 'İnce Memed', YazarID: 4, TurID: 1, ISBN: '978-975-10-0006-6', SayfaSayisi: 468, StokAdedi: 3 },
    { KitapID: 7, KitapAdi: 'Yer Demir Gök Bakır', YazarID: 4, TurID: 1, ISBN: '978-975-10-0007-7', SayfaSayisi: 312, StokAdedi: 2 },
    { KitapID: 8, KitapAdi: 'Sinekli Bakkal', YazarID: 5, TurID: 1, ISBN: '978-975-10-0008-8', SayfaSayisi: 340, StokAdedi: 4 },
    { KitapID: 9, KitapAdi: 'Ateşten Gömlek', YazarID: 5, TurID: 5, ISBN: '978-975-10-0009-9', SayfaSayisi: 224, StokAdedi: 3 },
    { KitapID: 10, KitapAdi: 'Kuyucaklı Yusuf', YazarID: 3, TurID: 1, ISBN: '978-975-10-0010-0', SayfaSayisi: 196, StokAdedi: 5 }
];

const now = new Date();
const oduncIslemleri = [
    { IslemID: 1, KullaniciID: 2, KitapID: 1, OduncTarihi: new Date(now - 20*86400000).toISOString(), TeslimTarihi: new Date(now - 6*86400000).toISOString(), TeslimEdildiMi: false },
    { IslemID: 2, KullaniciID: 3, KitapID: 5, OduncTarihi: new Date(now - 15*86400000).toISOString(), TeslimTarihi: new Date(now - 1*86400000).toISOString(), TeslimEdildiMi: false },
    { IslemID: 3, KullaniciID: 2, KitapID: 4, OduncTarihi: new Date(now - 5*86400000).toISOString(), TeslimTarihi: new Date(now + 9*86400000).toISOString(), TeslimEdildiMi: false }
];

// =============================================
// YARDIMCI FONKSİYONLAR
// =============================================

function yazarBul(yazarId) {
    const y = yazarlar.find(y => y.YazarID === yazarId);
    return y ? `${y.Ad} ${y.Soyad}` : '';
}

function turBul(turId) {
    const t = turler.find(t => t.TurID === turId);
    return t ? t.TurAdi : '';
}

function kitapDetay(k) {
    return { ...k, YazarAdSoyad: yazarBul(k.YazarID), TurAdi: turBul(k.TurID) };
}

// =============================================
// MIDDLEWARE
// =============================================

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

function adminMiddleware(req, res, next) {
    if (req.kullanici.rol !== 'admin') {
        return res.status(403).json({ hata: 'Bu işlem için admin yetkisi gerekli' });
    }
    next();
}

// =============================================
// İSTEK LOGLAMA
// =============================================
app.use((req, res, next) => {
    console.log(`[${new Date().toLocaleTimeString()}] ${req.method} ${req.url}`);
    next();
});

// =============================================
// TEST ENDPOINT
// =============================================
app.get('/api/test', (req, res) => {
    res.json({ mesaj: 'LibroQuest API çalışıyor (Demo Mod)' });
});

// =============================================
// AUTH ENDPOINT'LERİ
// =============================================

app.post('/api/giris', (req, res) => {
    const { kullaniciAdi, sifre } = req.body;
    const kullanici = kullanicilar.find(k => k.KullaniciAdi === kullaniciAdi);

    if (!kullanici) return res.status(401).json({ hata: 'Kullanıcı bulunamadı' });
    if (kullanici.Sifre !== sifre) return res.status(401).json({ hata: 'Şifre hatalı' });

    const token = jwt.sign(
        { id: kullanici.KullaniciID, kullaniciAdi: kullanici.KullaniciAdi, rol: kullanici.Rol, ad: kullanici.Ad },
        JWT_SECRET, { expiresIn: '24h' }
    );

    res.json({
        mesaj: 'Giriş başarılı',
        token,
        kullanici: {
            id: kullanici.KullaniciID, ad: kullanici.Ad, soyad: kullanici.Soyad,
            kullaniciAdi: kullanici.KullaniciAdi, rol: kullanici.Rol, puan: kullanici.Puan,
            cinsiyet: kullanici.Cinsiyet, sinif: kullanici.Sinif
        }
    });
});

app.post('/api/kayit', (req, res) => {
    const { ad, soyad, cinsiyet, sinif, kullaniciAdi, sifre, guvenlikAnahtari, cihazParmakIzi } = req.body;

    if (kullanicilar.find(k => k.KullaniciAdi === kullaniciAdi)) {
        return res.status(400).json({ hata: 'Bu kullanıcı adı zaten kullanılıyor' });
    }

    const yeniKullanici = {
        KullaniciID: nextKullaniciId++, Ad: ad, Soyad: soyad, Cinsiyet: cinsiyet,
        Sinif: sinif, KullaniciAdi: kullaniciAdi, Sifre: sifre, GuvenlikAnahtari: guvenlikAnahtari || '', CihazParmakIzi: cihazParmakIzi || '', Rol: 'kullanici', Puan: 100
    };
    kullanicilar.push(yeniKullanici);

    res.status(201).json({ mesaj: 'Kayıt başarılı! 100 puan hesabınıza tanımlandı.', kullaniciId: yeniKullanici.KullaniciID });
});

app.post('/api/sifremi-unuttum', (req, res) => {
    const { kullaniciAdi, guvenlikAnahtari, cihazParmakIzi, yeniSifre } = req.body;
    const kullanici = kullanicilar.find(k => k.KullaniciAdi === kullaniciAdi);
    if (!kullanici) return res.status(404).json({ hata: 'Kullanıcı bulunamadı' });
    if (kullanici.GuvenlikAnahtari !== guvenlikAnahtari) return res.status(403).json({ hata: 'Güvenlik anahtarı yanlış' });
    if (kullanici.CihazParmakIzi && kullanici.CihazParmakIzi !== cihazParmakIzi) return res.status(403).json({ hata: 'Bu cihazdan şifre sıfırlanamaz. Kayıt olduğunuz cihazı kullanın.' });
    kullanici.Sifre = yeniSifre;
    res.json({ mesaj: 'Şifre başarıyla güncellendi' });
});

// =============================================
// KULLANICI ENDPOINT'LERİ
// =============================================

app.get('/api/kullanicilar', authMiddleware, adminMiddleware, (req, res) => {
    res.json(kullanicilar.map(k => ({
        KullaniciID: k.KullaniciID, Ad: k.Ad, Soyad: k.Soyad, Cinsiyet: k.Cinsiyet,
        Sinif: k.Sinif, KullaniciAdi: k.KullaniciAdi, Rol: k.Rol, Puan: k.Puan
    })));
});

app.get('/api/kullanicilar/:id', authMiddleware, (req, res) => {
    const k = kullanicilar.find(k => k.KullaniciID === parseInt(req.params.id));
    if (!k) return res.status(404).json({ hata: 'Kullanıcı bulunamadı' });
    res.json({ KullaniciID: k.KullaniciID, Ad: k.Ad, Soyad: k.Soyad, Cinsiyet: k.Cinsiyet, Sinif: k.Sinif, KullaniciAdi: k.KullaniciAdi, Rol: k.Rol, Puan: k.Puan });
});

app.put('/api/kullanicilar/:id', authMiddleware, adminMiddleware, (req, res) => {
    const k = kullanicilar.find(k => k.KullaniciID === parseInt(req.params.id));
    if (!k) return res.status(404).json({ hata: 'Kullanıcı bulunamadı' });
    Object.assign(k, req.body);
    res.json({ mesaj: 'Kullanıcı güncellendi' });
});

app.delete('/api/kullanicilar/:id', authMiddleware, adminMiddleware, (req, res) => {
    const idx = kullanicilar.findIndex(k => k.KullaniciID === parseInt(req.params.id));
    if (idx === -1) return res.status(404).json({ hata: 'Kullanıcı bulunamadı' });
    kullanicilar.splice(idx, 1);
    res.json({ mesaj: 'Kullanıcı silindi' });
});

// =============================================
// YAZAR ENDPOINT'LERİ
// =============================================

app.get('/api/yazarlar', authMiddleware, (req, res) => {
    res.json(yazarlar);
});

app.post('/api/yazarlar', authMiddleware, adminMiddleware, (req, res) => {
    const { ad, soyad } = req.body;
    const yeni = { YazarID: nextYazarId++, Ad: ad, Soyad: soyad };
    yazarlar.push(yeni);
    res.status(201).json({ mesaj: 'Yazar eklendi', yazarId: yeni.YazarID });
});

app.delete('/api/yazarlar/:id', authMiddleware, adminMiddleware, (req, res) => {
    const idx = yazarlar.findIndex(y => y.YazarID === parseInt(req.params.id));
    if (idx === -1) return res.status(404).json({ hata: 'Yazar bulunamadı' });
    yazarlar.splice(idx, 1);
    res.json({ mesaj: 'Yazar silindi' });
});

// =============================================
// KİTAP TÜRLERİ ENDPOINT'LERİ
// =============================================

app.get('/api/turler', authMiddleware, (req, res) => {
    res.json(turler);
});

app.post('/api/turler', authMiddleware, adminMiddleware, (req, res) => {
    const { turAdi } = req.body;
    const yeni = { TurID: nextTurId++, TurAdi: turAdi };
    turler.push(yeni);
    res.status(201).json({ mesaj: 'Tür eklendi', turId: yeni.TurID });
});

// =============================================
// KİTAP ENDPOINT'LERİ
// =============================================

app.get('/api/kitaplar', authMiddleware, (req, res) => {
    res.json(kitaplar.map(kitapDetay));
});

app.get('/api/kitaplar/:id', authMiddleware, (req, res) => {
    const k = kitaplar.find(k => k.KitapID === parseInt(req.params.id));
    if (!k) return res.status(404).json({ hata: 'Kitap bulunamadı' });
    res.json(kitapDetay(k));
});

app.post('/api/kitaplar', authMiddleware, adminMiddleware, (req, res) => {
    const { kitapAdi, yazarId, turId, isbn, sayfaSayisi, stokAdedi } = req.body;
    const yeni = { KitapID: nextKitapId++, KitapAdi: kitapAdi, YazarID: yazarId, TurID: turId, ISBN: isbn, SayfaSayisi: sayfaSayisi, StokAdedi: stokAdedi || 1 };
    kitaplar.push(yeni);
    res.status(201).json({ mesaj: 'Kitap eklendi', kitapId: yeni.KitapID });
});

app.delete('/api/kitaplar/:id', authMiddleware, adminMiddleware, (req, res) => {
    const idx = kitaplar.findIndex(k => k.KitapID === parseInt(req.params.id));
    if (idx === -1) return res.status(404).json({ hata: 'Kitap bulunamadı' });
    kitaplar.splice(idx, 1);
    res.json({ mesaj: 'Kitap silindi' });
});

// =============================================
// ÖDÜNÇ İŞLEMLERİ ENDPOINT'LERİ
// =============================================

app.get('/api/odunc', authMiddleware, (req, res) => {
    const sonuc = oduncIslemleri.map(o => {
        const k = kullanicilar.find(k => k.KullaniciID === o.KullaniciID);
        const kt = kitaplar.find(kt => kt.KitapID === o.KitapID);
        return { ...o, UyeAdSoyad: k ? `${k.Ad} ${k.Soyad}` : '', KitapAdi: kt ? kt.KitapAdi : '', YazarAdSoyad: kt ? yazarBul(kt.YazarID) : '' };
    });
    res.json(sonuc);
});

app.get('/api/odunc/kullanici/:id', authMiddleware, (req, res) => {
    const id = parseInt(req.params.id);
    const sonuc = oduncIslemleri.filter(o => o.KullaniciID === id).map(o => {
        const kt = kitaplar.find(kt => kt.KitapID === o.KitapID);
        const now = new Date();
        const teslim = new Date(o.TeslimTarihi);
        const diffMs = teslim - now;
        const diffDays = Math.ceil(diffMs / 86400000);
        return {
            ...o,
            KitapAdi: kt ? kt.KitapAdi : '',
            YazarAdSoyad: kt ? yazarBul(kt.YazarID) : '',
            TurAdi: kt ? turBul(kt.TurID) : '',
            GecikmeGunu: diffDays < 0 ? Math.abs(diffDays) : 0,
            KalanGun: diffDays > 0 ? diffDays : 0
        };
    });
    res.json(sonuc);
});

app.post('/api/odunc', authMiddleware, adminMiddleware, (req, res) => {
    const { kullaniciId, kitapId, teslimTarihi } = req.body;
    const yeni = {
        IslemID: nextIslemId++, KullaniciID: kullaniciId, KitapID: kitapId,
        OduncTarihi: new Date().toISOString(), TeslimTarihi: new Date(teslimTarihi).toISOString(), TeslimEdildiMi: false
    };
    oduncIslemleri.push(yeni);
    res.status(201).json({ mesaj: 'Kitap ödünç verildi', islemId: yeni.IslemID });
});

app.put('/api/odunc/:id/teslim', authMiddleware, adminMiddleware, (req, res) => {
    const islem = oduncIslemleri.find(o => o.IslemID === parseInt(req.params.id));
    if (!islem) return res.status(404).json({ hata: 'İşlem bulunamadı' });

    islem.TeslimEdildiMi = true;
    const now = new Date();
    const teslim = new Date(islem.TeslimTarihi);
    let puanDususu = 0;

    if (now > teslim) {
        const gecikmeGunu = Math.ceil((now - teslim) / 86400000);
        if (gecikmeGunu >= 1 && gecikmeGunu <= 3) puanDususu = gecikmeGunu * 5;
        else if (gecikmeGunu >= 4 && gecikmeGunu <= 7) puanDususu = 15 + (gecikmeGunu - 3) * 10;
        else if (gecikmeGunu > 7) puanDususu = 55 + (gecikmeGunu - 7) * 15;
    }

    if (puanDususu > 0) {
        const k = kullanicilar.find(k => k.KullaniciID === islem.KullaniciID);
        if (k) k.Puan = Math.max(0, k.Puan - puanDususu);
    }

    res.json({ mesaj: 'Kitap teslim alındı', puanDususu, gecikmeVar: puanDususu > 0 });
});

// =============================================
// GECİKEN KİTAPLAR ENDPOINT'İ
// =============================================

app.get('/api/gecikenler', authMiddleware, (req, res) => {
    const now = new Date();
    const gecikenler = oduncIslemleri
        .filter(o => !o.TeslimEdildiMi && new Date(o.TeslimTarihi) < now)
        .map(o => {
            const k = kullanicilar.find(k => k.KullaniciID === o.KullaniciID);
            const kt = kitaplar.find(kt => kt.KitapID === o.KitapID);
            const gecikmeGunu = Math.ceil((now - new Date(o.TeslimTarihi)) / 86400000);
            let puanDususu = 0;
            if (gecikmeGunu >= 1 && gecikmeGunu <= 3) puanDususu = gecikmeGunu * 5;
            else if (gecikmeGunu >= 4 && gecikmeGunu <= 7) puanDususu = 15 + (gecikmeGunu - 3) * 10;
            else if (gecikmeGunu > 7) puanDususu = 55 + (gecikmeGunu - 7) * 15;

            return {
                IslemID: o.IslemID, KullaniciID: o.KullaniciID,
                UyeAdSoyad: k ? `${k.Ad} ${k.Soyad}` : '', Sinif: k ? k.Sinif : '', Cinsiyet: k ? k.Cinsiyet : '', Puan: k ? k.Puan : 0,
                KitapAdi: kt ? kt.KitapAdi : '', KitapID: o.KitapID, YazarAdSoyad: kt ? yazarBul(kt.YazarID) : '',
                OduncTarihi: o.OduncTarihi, TeslimTarihi: o.TeslimTarihi, GecikmeGunu: gecikmeGunu, PuanDususu: puanDususu
            };
        })
        .sort((a, b) => b.GecikmeGunu - a.GecikmeGunu);
    res.json(gecikenler);
});

// =============================================
// İSTATİSTİK ENDPOINT'İ
// =============================================

app.get('/api/istatistikler', authMiddleware, adminMiddleware, (req, res) => {
    const now = new Date();
    res.json({
        toplamKitap: kitaplar.length,
        toplamUye: kullanicilar.filter(k => k.Rol === 'kullanici').length,
        aktifOdunc: oduncIslemleri.filter(o => !o.TeslimEdildiMi).length,
        gecikenSayisi: oduncIslemleri.filter(o => !o.TeslimEdildiMi && new Date(o.TeslimTarihi) < now).length
    });
});

// =============================================
// SUNUCU BAŞLAT
// =============================================
const PORT = 3000;

app.listen(PORT, '0.0.0.0', () => {
    console.log('');
    console.log('============================================');
    console.log('  LibroQuest API - DEMO MOD (In-Memory)');
    console.log('============================================');
    console.log(`  Sunucu: http://localhost:${PORT}`);
    console.log(`  Ağ:     http://192.168.1.106:${PORT}`);
    console.log('');
    console.log('  Hazır Hesaplar:');
    console.log('  Admin:     admin / admin123');
    console.log('  Kullanıcı: ayse.yilmaz / 123456');
    console.log('  Kullanıcı: kerem.demir / 123456');
    console.log('  Kullanıcı: zeynep.kara / 123456');
    console.log('============================================');
    console.log('');
    console.log('  SQL Server gerekmiyor, veriler bellekte.');
    console.log('  Sunucuyu kapatınca veriler sıfırlanır.');
    console.log('');
});
