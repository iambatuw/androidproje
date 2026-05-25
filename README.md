# LibroQuest - Kütüphane Yönetim Sistemi

Android (Kotlin) + Node.js Backend API + SQL Server Express veritabanı ile geliştirilmiş tam kapsamlı kütüphane yönetim sistemi.

---

## Proje Yapısı

```
LibroQuest/
├── app/                              # Android Uygulaması (Kotlin)
│   └── src/main/
│       ├── java/com/libroquest/app/
│       │   ├── activities/           # 10 Activity (Login, Register, Dashboard, vs.)
│       │   ├── adapters/             # 4 RecyclerView Adapter
│       │   ├── api/                  # Retrofit API + SessionManager
│       │   └── models/               # 6 Veri Modeli
│       └── res/
│           ├── layout/               # 16 XML Layout
│           ├── drawable/             # Arka plan, ikon drawable'ları
│           └── values/               # Renkler, stringler, tema
│
├── backend/                          # Node.js API Sunucusu
│   ├── server.js                     # SQL Server ile çalışan gerçek API
│   ├── server-demo.js                # In-memory demo API (SQL Server gerektirmez!)
│   ├── database.sql                  # Veritabanı oluşturma script'i
│   ├── package.json                  # Node.js bağımlılıkları
│   └── .env                          # Veritabanı bağlantı ayarları
│
├── build.gradle.kts                  # Android root gradle
├── settings.gradle.kts
├── KURULUM_REHBERI.md                # Detaylı kurulum rehberi
└── README.md                         # Bu dosya
```

---

## Gereksinimler

| Araç | Açıklama |
|------|----------|
| **Android Studio** | Android uygulamasını build etmek için |
| **Node.js** (v18+) | Backend API sunucusu için |
| **SQL Server Express** | Sadece `server.js` kullanacaksan (demo mod için GEREKMEZ) |

---

## HIZLI BAŞLANGIÇ (Demo Mod)

> SQL Server kurulumu **gerekmez**. Veriler bellekte tutulur.

### Adım 1: Backend Bağımlılıklarını Kur
```bash
cd C:\Users\batuh\AndroidStudioProjects\LibroQuest\backend
npm install
```

### Adım 2: Demo Sunucuyu Başlat
```bash
node server-demo.js
```
Çıktı:
```
============================================
  LibroQuest API - DEMO MOD (In-Memory)
============================================
  Sunucu: http://localhost:3000
  Ağ:     http://192.168.1.106:3000
============================================
```

### Adım 3: Windows Firewall'da Port 3000'i Aç
> **Bu adım zorunlu!** Telefon, bilgisayardaki sunucuya ancak port açıksa ulaşabilir.

1. `Win + R` → `wf.msc` yaz → Enter
2. Sol menü → **Inbound Rules** → Sağ menü → **New Rule...**
3. **Port** seç → Next
4. **TCP** seç → Specific local ports: **3000** → Next
5. **Allow the connection** → Next
6. Domain, Private, Public hepsi **seçili** → Next
7. Name: **LibroQuest API** → Finish

### Adım 4: IP Adresini Ayarla

Bilgisayarın IP adresini bul (CMD veya PowerShell):
```bash
ipconfig
```
`IPv4 Address` satırındaki IP'yi not al (örn: `192.168.1.106`).

Bu IP'yi şu dosyaya yaz:

**Dosya:** `app/src/main/java/com/libroquest/app/api/RetrofitClient.kt`
**Satır 12:**
```kotlin
private const val BASE_URL = "http://192.168.1.106:3000/"
```

> **Emülatör kullanıyorsan:** `http://10.0.2.2:3000/` yaz.
> **Gerçek telefon kullanıyorsan:** Bilgisayarın WiFi IP'sini yaz.
> **ÖNEMLİ:** IP değiştikten sonra uygulamayı **yeniden Run (▶)** etmen gerekir!

### Adım 5: Uygulamayı Çalıştır
1. Android Studio'da projeyi aç
2. Telefonunu USB ile bağla (veya emülatör seç)
3. **Run (▶)** butonuna bas
4. Giriş ekranında aşağıdaki hesaplardan birini kullan

### Adım 6: Test Et
Tarayıcıdan kontrol:
- PC: http://localhost:3000/api/test
- Telefon: http://192.168.1.106:3000/api/test
- Sonuç: `{"mesaj":"LibroQuest API çalışıyor (Demo Mod)"}`

---

## Hazır Hesaplar

| Kullanıcı Adı | Şifre    | Rol       | Puan |
|----------------|----------|-----------|------|
| admin          | admin123 | Admin     | 100  |
| ayse.yilmaz    | 123456   | Kullanıcı | 100  |
| kerem.demir    | 123456   | Kullanıcı | 85   |
| zeynep.kara    | 123456   | Kullanıcı | 95   |

Yeni hesap da kayıt ekranından oluşturulabilir (otomatik 100 puan verilir).

---

## SQL Server ile Çalıştırma (Gerçek Veritabanı)

Demo mod yerine kalıcı veritabanıyla çalıştırmak istersen:

### 1. SQL Server Express Kur
- İndir: https://www.microsoft.com/en-us/sql-server/sql-server-downloads → **Express** seç
- SSMS İndir: https://learn.microsoft.com/en-us/sql/ssms/download-sql-server-management-studio-ssms

### 2. SQL Server Yapılandırması
1. **SSMS** aç → Server'a sağ tıkla → **Properties**
2. **Security** sekmesi → **SQL Server and Windows Authentication mode** seç → OK
3. **Security → Logins → sa** sağ tıkla → Properties:
   - **General** → Şifre belirle (bunu .env'ye yazacaksın)
   - **Status** → Login: **Enabled** → OK
4. **SQL Server Configuration Manager** aç:
   - SQL Server Network Configuration → Protocols for SQLEXPRESS
   - **TCP/IP** → Sağ tıkla → **Enable**
   - **TCP/IP** → Properties → **IP Addresses** → En altta **IPAll**:
     - TCP Dynamic Ports: **boş bırak**
     - TCP Port: **1433**
5. SQL Server Services → **SQL Server (SQLEXPRESS)** → Sağ tıkla → **Restart**

### 3. Veritabanını Oluştur
1. SSMS'de **sa** hesabıyla bağlan (SQL Server Authentication)
2. **File → Open →** `backend/database.sql` dosyasını aç
3. **F5** ile çalıştır → LibroQuestDB veritabanı + tüm tablolar + örnek veriler oluşur

### 4. .env Dosyasını Düzenle

**Dosya:** `backend/.env`
```env
DB_SERVER=localhost\\SQLEXPRESS
DB_NAME=LibroQuestDB
DB_USER=sa
DB_PASSWORD=BURAYA_SA_SIFRESINI_YAZ
DB_PORT=1433
JWT_SECRET=libroquest_secret_key_2024
PORT=3000
```

### 5. Gerçek Sunucuyu Başlat
```bash
cd C:\Users\batuh\AndroidStudioProjects\LibroQuest\backend
npm install
node server.js
```
> `server-demo.js` yerine `server.js` kullanıyorsun. Bu SQL Server'a bağlanır.

Başarılı çıktı:
```
SQL Server Express bağlantısı başarılı!
LibroQuest API sunucusu 3000 portunda çalışıyor
```

### 6. Firewall + IP Ayarları
Yukarıdaki "Hızlı Başlangıç" bölümündeki Adım 3 ve 4 aynı şekilde yapılır.

---

## Uygulama Ekranları

### Giriş & Kayıt
| Ekran | Açıklama |
|-------|----------|
| **Giriş Ekranı** | Kullanıcı adı + şifre, giriş yap butonu |
| **Kayıt Ekranı** | Ad, Soyad, Cinsiyet, Sınıf, Kullanıcı Adı, Şifre (100 puan otomatik) |
| **Şifre Sıfırlama** | Kullanıcı adıyla yeni şifre belirleme |

### Admin Paneli (6 Buton)
| Buton | İşlev |
|-------|-------|
| **Kitap Ödünç Ver** | Üye seç → Kitap seç → Teslim tarihi seç |
| **Kitap Kayıt Et** | Kitap adı, yazar, tür, ISBN, sayfa, stok |
| **Yazar Kaydı** | Yazar ekleme, listeleme, silme |
| **Kitap Listesi** | Tüm kitaplar (yazar, tür, stok bilgisi) |
| **Gecikenler Listesi** | Geciken kitaplar + puan düşüşü bilgisi |
| **Üye Yönetimi** | Tüm üyelerin puan ve bilgileri |

### Kullanıcı Paneli (6 Buton)
| Buton | İşlev |
|-------|-------|
| **Ödünç Kitaplarım** | Kişinin ödünç aldığı aktif kitaplar |
| **Kitap Kataloğu** | Tüm kitapları görüntüleme |
| **Kitap Türleri** | Mevcut türleri listeleme |
| **Puanlarım** | Puan bilgisi ve kuralları |
| **Profilim** | Kişisel bilgiler |
| **Çıkış Yap** | Oturumu kapat |

---

## Güvenlik Sistemi (Cihaz Parmak İzi + Güvenlik Anahtarı)

Şifre sıfırlama işlemi **2 katmanlı doğrulama** ile korunur:

### Kayıt Aşaması
Kullanıcı kayıt olurken:
1. **Güvenlik Anahtarı** cihaz bilgilerinden otomatik oluşturulur ve alana yazılır (8 karakterlik kod, örn: `A3F8C2D1`)
2. Uygulama arka planda cihazın **parmak izini** (device fingerprint) de otomatik oluşturur ve sunucuya gönderir
3. Kullanıcının bir şey girmesine gerek yoktur — her şey otomatik

### Cihaz Parmak İzi Nasıl Oluşuyor?
Cihaza özgü donanım bilgileri toplanır ve SHA-256 ile hash'lenir:

```
Toplanan bilgiler:
  - ANDROID_ID (cihaza özel benzersiz ID)
  - BRAND      (marka: Xiaomi, Samsung, vs.)
  - MODEL      (model: Redmi Note 12, vs.)
  - DEVICE     (cihaz kodu)
  - HARDWARE   (donanım bilgisi)
  - MANUFACTURER (üretici)

İşlem:
  "android_id|brand|model|device|hardware|manufacturer"
       ↓
  SHA-256 hash → "a3f8c2d1e5b7..."  (64 karakterlik benzersiz parmak izi)
```

> **Dosya:** `app/src/main/java/com/libroquest/app/api/CihazBilgisi.kt`

### Şifre Sıfırlama (2 Katmanlı Doğrulama)

```
Kullanıcı "Şifremi Unuttum" ekranına girer
       ↓
1. Kullanıcı adını girer
2. Güvenlik anahtarını girer
3. Yeni şifresini belirler
       ↓
Sunucu kontrol eder:
  ✅ Güvenlik anahtarı doğru mu?  → Yanlışsa: "Güvenlik anahtarı yanlış!"
  ✅ Cihaz parmak izi eşleşiyor mu? → Farklı cihazsa: "Bu cihazdan şifre sıfırlanamaz!"
  ✅ İkisi de doğruysa → Şifre güncellenir
```

### Neden Bu Sistem?
- Sadece kullanıcı adını bilen biri şifreyi **değiştiremez** (güvenlik anahtarı gerekli)
- Güvenlik anahtarını bilen biri bile **başka telefondan** sıfırlayamaz (cihaz parmak izi eşleşmez)
- Kayıt olan cihaz + doğru anahtar = **şifre sıfırlama izni**

---

## Puan Sistemi

| Durum | Puan |
|-------|------|
| Kayıt olunca | **+100 puan** |
| 1-3 gün gecikme | Günlük **-5 puan** |
| 4-7 gün gecikme | Günlük **-10 puan** |
| 7+ gün gecikme | **Hesap askıya alınır** |

---

## Veritabanı Tabloları

| Tablo | Alanlar |
|-------|---------|
| **Kullanicilar** | KullaniciID, Ad, Soyad, Cinsiyet, Sinif, KullaniciAdi, Sifre, GuvenlikAnahtari, CihazParmakIzi, Rol, Puan |
| **Yazarlar** | YazarID, Ad, Soyad |
| **KitapTurleri** | TurID, TurAdi |
| **Kitaplar** | KitapID, KitapAdi, YazarID, TurID, ISBN, SayfaSayisi, StokAdedi |
| **OduncIslemleri** | IslemID, KullaniciID, KitapID, OduncTarihi, TeslimTarihi, TeslimEdildiMi |
| **PuanGecmisi** | PuanID, KullaniciID, IslemID, PuanDegisimi, Aciklama |

---

## Kullanılan Teknolojiler

| Katman | Teknoloji |
|--------|-----------|
| **Android** | Kotlin, Material Design 3, Retrofit 2, OkHttp, Gson, RecyclerView |
| **Backend** | Node.js, Express.js, JSON Web Token (JWT), bcryptjs |
| **Veritabanı** | SQL Server Express (gerçek) / In-Memory (demo) |
| **Mimari** | REST API, MVC, SharedPreferences (oturum), Role-Based Access |

---

## Tasarım Sistemi

| Özellik | Değer |
|---------|-------|
| **Primary** | #00163C (Deep Sapphire) |
| **Surface** | #FBF9F8 (Soft Ivory) |
| **Secondary** | #0F2B5B |
| **Error** | #BA1A1A |
| **Tema** | Material Design 3 |
| **Stil** | Modern Corporate + Tactile |

---

## Sorun Giderme

| Sorun | Çözüm |
|-------|-------|
| "Sunucuya bağlanılamadı" | 1) `node server-demo.js` çalışıyor mu? 2) Firewall'da port 3000 açık mı? 3) Aynı WiFi'da mısın? |
| Kayıt/Giriş çalışmıyor | Uygulamayı yeniden **Run (▶)** et (IP değişikliği build gerektirir) |
| IP adresi değişti | CMD'de `ipconfig` yaz → `RetrofitClient.kt` satır 12'yi güncelle → Yeniden Run |
| Telefonda bağlanmıyor ama PC'de çalışıyor | Firewall kuralı eksik (port 3000 aç) |
| Emülatörde çalışmıyor | BASE_URL'i `http://10.0.2.2:3000/` yap |
| Demo veriler kayboldu | `server-demo.js` yeniden başlat (veriler bellekte, kapanınca sıfırlanır) |
| SQL Server bağlantı hatası | TCP/IP aktif mi? Port 1433 mü? sa şifresi doğru mu? |

---

## Lisans
Bu proje eğitim amaçlı geliştirilmiştir.
