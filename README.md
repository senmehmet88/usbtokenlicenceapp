# usbtokenlicenceapp
USBTokenLicenceApp

SafeNet Token ile (etpkcs11.dll driverı) yapılan lisanslama uygulaması.

# Token Initialize
tr.com.msen.ytu.crypto.AppInitialize sınıfı args[0] = "etpkcs11.dll" , args[1] = tokenLabel değeri ile çalıştırılarak token initialize ediliyor. 
Rastgele pin atanarak pin.txt dosyasına yazılıyor. 
Verilen label token'a atanıyor. 
Token içerisinde AES SecretKey oluşturuluyor. 
Oluşturulan gizli anahtar ile test-ytu.bin (binary c++ uygulaması) dosyası şifreleniyor.

# Decrypt and Run
tr.com.msen.ytu.crypto.AppDecryptAndRun sınıfı args[0] = "etpkcs11.dll" ve args[1] = pin değeri ile çalıştırılarak uygulama token aracılığıyla çalıştırılabilmektedir.
