-- =============================================
-- LibroQuest Kütüphane Yönetim Sistemi
-- SQL Server Express Veritabanı Oluşturma Script'i
-- =============================================

CREATE DATABASE LibroQuestDB;
GO

USE LibroQuestDB;
GO

-- =============================================
-- TABLO 1: Kullanicilar (Üye Bilgileri)
-- =============================================
CREATE TABLE Kullanicilar (
    KullaniciID INT IDENTITY(1,1) PRIMARY KEY,
    Ad NVARCHAR(50) NOT NULL,
    Soyad NVARCHAR(50) NOT NULL,
    Cinsiyet NVARCHAR(20),
    Sinif NVARCHAR(20),
    KullaniciAdi NVARCHAR(50) UNIQUE NOT NULL,
    Sifre NVARCHAR(255) NOT NULL,
    Rol NVARCHAR(20) DEFAULT 'kullanici',  -- 'admin' veya 'kullanici'
    Puan INT DEFAULT 100,
    KayitTarihi DATETIME DEFAULT GETDATE()
);
GO

-- =============================================
-- TABLO 2: Yazarlar
-- =============================================
CREATE TABLE Yazarlar (
    YazarID INT IDENTITY(1,1) PRIMARY KEY,
    Ad NVARCHAR(50) NOT NULL,
    Soyad NVARCHAR(50) NOT NULL,
    KayitTarihi DATETIME DEFAULT GETDATE()
);
GO

-- =============================================
-- TABLO 3: Kitap Türleri (Kategoriler)
-- =============================================
CREATE TABLE KitapTurleri (
    TurID INT IDENTITY(1,1) PRIMARY KEY,
    TurAdi NVARCHAR(50) NOT NULL UNIQUE
);
GO

-- =============================================
-- TABLO 4: Kitaplar
-- =============================================
CREATE TABLE Kitaplar (
    KitapID INT IDENTITY(1,1) PRIMARY KEY,
    KitapAdi NVARCHAR(200) NOT NULL,
    YazarID INT FOREIGN KEY REFERENCES Yazarlar(YazarID),
    TurID INT FOREIGN KEY REFERENCES KitapTurleri(TurID),
    ISBN NVARCHAR(20),
    SayfaSayisi INT,
    StokAdedi INT DEFAULT 1,
    KayitTarihi DATETIME DEFAULT GETDATE()
);
GO

-- =============================================
-- TABLO 5: Ödünç İşlemleri
-- =============================================
CREATE TABLE OduncIslemleri (
    IslemID INT IDENTITY(1,1) PRIMARY KEY,
    KullaniciID INT FOREIGN KEY REFERENCES Kullanicilar(KullaniciID),
    KitapID INT FOREIGN KEY REFERENCES Kitaplar(KitapID),
    OduncTarihi DATETIME DEFAULT GETDATE(),
    TeslimTarihi DATETIME NOT NULL,
    GercekTeslimTarihi DATETIME NULL,
    TeslimEdildiMi BIT DEFAULT 0
);
GO

-- =============================================
-- TABLO 6: Puan Geçmişi (Puan hareketleri)
-- =============================================
CREATE TABLE PuanGecmisi (
    PuanID INT IDENTITY(1,1) PRIMARY KEY,
    KullaniciID INT FOREIGN KEY REFERENCES Kullanicilar(KullaniciID),
    IslemID INT FOREIGN KEY REFERENCES OduncIslemleri(IslemID),
    PuanDegisimi INT NOT NULL,
    Aciklama NVARCHAR(200),
    Tarih DATETIME DEFAULT GETDATE()
);
GO

-- =============================================
-- ÖRNEK VERİLER
-- =============================================

-- Admin kullanıcı (şifre: admin123)
INSERT INTO Kullanicilar (Ad, Soyad, Cinsiyet, Sinif, KullaniciAdi, Sifre, Rol, Puan)
VALUES (N'Admin', N'Yönetici', N'Erkek', N'Yönetim', N'admin', N'admin123', N'admin', 100);

-- Örnek kullanıcılar
INSERT INTO Kullanicilar (Ad, Soyad, Cinsiyet, Sinif, KullaniciAdi, Sifre, Rol, Puan)
VALUES (N'Ayşe', N'Yılmaz', N'Kadın', N'10/A', N'ayse.yilmaz', N'123456', N'kullanici', 80);

INSERT INTO Kullanicilar (Ad, Soyad, Cinsiyet, Sinif, KullaniciAdi, Sifre, Rol, Puan)
VALUES (N'Kerem', N'Demir', N'Erkek', N'11/B', N'kerem.demir', N'123456', N'kullanici', 95);

INSERT INTO Kullanicilar (Ad, Soyad, Cinsiyet, Sinif, KullaniciAdi, Sifre, Rol, Puan)
VALUES (N'Zeynep', N'Kara', N'Kadın', N'12/A', N'zeynep.kara', N'123456', N'kullanici', 100);

-- Örnek yazarlar
INSERT INTO Yazarlar (Ad, Soyad) VALUES (N'Fyodor', N'Dostoyevski');
INSERT INTO Yazarlar (Ad, Soyad) VALUES (N'Yuval Noah', N'Harari');
INSERT INTO Yazarlar (Ad, Soyad) VALUES (N'Orhan', N'Pamuk');
INSERT INTO Yazarlar (Ad, Soyad) VALUES (N'Sabahattin', N'Ali');
INSERT INTO Yazarlar (Ad, Soyad) VALUES (N'George', N'Orwell');
INSERT INTO Yazarlar (Ad, Soyad) VALUES (N'Harper', N'Lee');

-- Örnek kitap türleri
INSERT INTO KitapTurleri (TurAdi) VALUES (N'Roman');
INSERT INTO KitapTurleri (TurAdi) VALUES (N'Bilim');
INSERT INTO KitapTurleri (TurAdi) VALUES (N'Tarih');
INSERT INTO KitapTurleri (TurAdi) VALUES (N'Felsefe');
INSERT INTO KitapTurleri (TurAdi) VALUES (N'Edebiyat');
INSERT INTO KitapTurleri (TurAdi) VALUES (N'Bilim Kurgu');

-- Örnek kitaplar
INSERT INTO Kitaplar (KitapAdi, YazarID, TurID, ISBN, SayfaSayisi, StokAdedi)
VALUES (N'Suç ve Ceza', 1, 1, N'978-975-07-0255-5', 687, 3);

INSERT INTO Kitaplar (KitapAdi, YazarID, TurID, ISBN, SayfaSayisi, StokAdedi)
VALUES (N'Sapiens', 2, 2, N'978-605-09-2433-1', 464, 2);

INSERT INTO Kitaplar (KitapAdi, YazarID, TurID, ISBN, SayfaSayisi, StokAdedi)
VALUES (N'İstanbul', 3, 5, N'978-975-05-0454-3', 428, 2);

INSERT INTO Kitaplar (KitapAdi, YazarID, TurID, ISBN, SayfaSayisi, StokAdedi)
VALUES (N'Kürk Mantolu Madonna', 4, 1, N'978-975-10-0307-7', 160, 4);

INSERT INTO Kitaplar (KitapAdi, YazarID, TurID, ISBN, SayfaSayisi, StokAdedi)
VALUES (N'1984', 5, 6, N'978-975-07-0673-7', 352, 3);

INSERT INTO Kitaplar (KitapAdi, YazarID, TurID, ISBN, SayfaSayisi, StokAdedi)
VALUES (N'Bülbülü Öldürmek', 6, 1, N'978-975-07-1453-4', 372, 2);

-- Örnek ödünç işlemleri (bazıları gecikmiş)
INSERT INTO OduncIslemleri (KullaniciID, KitapID, OduncTarihi, TeslimTarihi, TeslimEdildiMi)
VALUES (2, 1, DATEADD(DAY, -20, GETDATE()), DATEADD(DAY, -4, GETDATE()), 0);

INSERT INTO OduncIslemleri (KullaniciID, KitapID, OduncTarihi, TeslimTarihi, TeslimEdildiMi)
VALUES (3, 2, DATEADD(DAY, -16, GETDATE()), DATEADD(DAY, -1, GETDATE()), 0);

INSERT INTO OduncIslemleri (KullaniciID, KitapID, OduncTarihi, TeslimTarihi, TeslimEdildiMi)
VALUES (2, 5, DATEADD(DAY, -5, GETDATE()), DATEADD(DAY, 9, GETDATE()), 0);

INSERT INTO OduncIslemleri (KullaniciID, KitapID, OduncTarihi, TeslimTarihi, TeslimEdildiMi)
VALUES (4, 4, DATEADD(DAY, -10, GETDATE()), DATEADD(DAY, 4, GETDATE()), 0);

GO

-- =============================================
-- VIEW: Geciken Kitaplar Görünümü
-- =============================================
CREATE VIEW vw_GecikenKitaplar AS
SELECT 
    o.IslemID,
    k.KullaniciID,
    k.Ad + ' ' + k.Soyad AS UyeAdSoyad,
    k.Sinif,
    k.Puan,
    kt.KitapAdi,
    y.Ad + ' ' + y.Soyad AS YazarAdSoyad,
    o.OduncTarihi,
    o.TeslimTarihi,
    DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) AS GecikmeGunu,
    CASE 
        WHEN DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) BETWEEN 1 AND 3 THEN DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) * 5
        WHEN DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) BETWEEN 4 AND 7 THEN 15 + ((DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) - 3) * 10)
        WHEN DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) > 7 THEN 55 + ((DATEDIFF(DAY, o.TeslimTarihi, GETDATE()) - 7) * 15)
        ELSE 0
    END AS PuanDususu
FROM OduncIslemleri o
INNER JOIN Kullanicilar k ON o.KullaniciID = k.KullaniciID
INNER JOIN Kitaplar kt ON o.KitapID = kt.KitapID
INNER JOIN Yazarlar y ON kt.YazarID = y.YazarID
WHERE o.TeslimEdildiMi = 0 AND o.TeslimTarihi < GETDATE();
GO

PRINT 'LibroQuest veritabanı başarıyla oluşturuldu!';
GO
