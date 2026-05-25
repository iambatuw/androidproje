# LibroQuest Kurulum Rehberi

## HIZLI BAŞLANGIÇ (Demo Mod - SQL Server GEREKMEZ)

### Adım 1: Sunucuyu Başlat
Komut satırını aç (CMD veya PowerShell):
```bash
cd C:\Users\batuh\AndroidStudioProjects\LibroQuest\backend
npm install
node server-demo.js
```
Şunu görmelisin:
```
============================================
  LibroQuest API - DEMO MOD (In-Memory)
============================================
  Sunucu: http://localhost:3000
  Ağ:     http://192.168.1.106:3000
```

### Adım 2: Firewall Ayarı (Telefondan Bağlanmak İçin)
1. Windows Arama → "Windows Defender Firewall with Advanced Security"
2. Sol menü → "Inbound Rules" → Sağ menü → "New Rule..."
3. Port → Next → TCP → Specific local ports: **3000** → Next
4. Allow the connection → Next → Hepsi seçili → Next
5. Name: **LibroQuest API** → Finish

### Adım 3: Test Et
- Bilgisayar tarayıcısında: http://localhost:3000/api/test
- Telefon tarayıcısında: http://192.168.1.106:3000/api/test
- "LibroQuest API çalışıyor (Demo Mod)" mesajını görmelisin

### Adım 4: Android Uygulamasını Çalıştır
1. Android Studio'da projeyi aç
2. Telefonunu USB ile bağla veya emülatör seç
3. Run (▶) butonuna bas
4. Giriş ekranında kullanıcı adı ve şifreyi gir

### Hazır Hesaplar
| Kullanıcı Adı | Şifre    | Rol       |
|----------------|----------|-----------|
| admin          | admin123 | Admin     |
| ayse.yilmaz    | 123456   | Kullanıcı |
| kerem.demir    | 123456   | Kullanıcı |
| zeynep.kara    | 123456   | Kullanıcı |

### ÖNEMLİ NOTLAR
- Telefon ve bilgisayar **aynı WiFi** ağında olmalı
- Sunucuyu kapatırsan veriler sıfırlanır (demo mod)
- Yeni kayıt da oluşturabilirsin (100 puan verilir)
- IP adresi değiştiyse: `RetrofitClient.kt` dosyasında `BASE_URL`'i güncelle
- Emülatör kullanıyorsan IP: `http://10.0.2.2:3000/`

---

## SQL SERVER İLE ÇALIŞTIRMA (Gerçek Kullanım)

Demo değil gerçek veritabanıyla çalıştırmak istersen:

### 1. SQL Server Express Kurulumu
- İndir: https://www.microsoft.com/en-us/sql-server/sql-server-downloads
- SSMS İndir: https://learn.microsoft.com/en-us/sql/ssms/download-sql-server-management-studio-ssms

### 2. SQL Server Yapılandırma
1. SSMS → Server sağ tık → Properties → Security → "SQL Server and Windows Authentication mode"
2. Security → Logins → sa → Properties → Status → Login: Enabled, şifre belirle
3. SQL Server Configuration Manager → Protocols for SQLEXPRESS → TCP/IP Enable
4. TCP/IP Properties → IP Addresses → IPAll → TCP Port: 1433
5. SQL Server servisini restart et

### 3. Veritabanını Oluştur
1. SSMS'de `backend/database.sql` dosyasını aç
2. F5 ile çalıştır

### 4. .env Dosyasını Düzenle
```
DB_SERVER=localhost\\SQLEXPRESS
DB_NAME=LibroQuestDB
DB_USER=sa
DB_PASSWORD=SENIN_SA_SIFREN
DB_PORT=1433
```

### 5. Gerçek Sunucuyu Başlat
```bash
cd backend
node server.js
```
(server-demo.js yerine server.js kullan)

---

## Sorun Giderme

| Sorun | Çözüm |
|-------|-------|
| Sunucuya bağlanılamadı | Firewall'da port 3000 açık mı? Aynı WiFi'da mısın? |
| Sürekli dönüyor | Backend çalışıyor mu kontrol et |
| IP değişti | `ipconfig` yaz, yeni IP'yi `RetrofitClient.kt`'ye yaz |
| Telefon bağlanmıyor ama PC bağlanıyor | Firewall kuralını ekle |
| Emülatörde çalışmıyor | BASE_URL'i `http://10.0.2.2:3000/` yap |
