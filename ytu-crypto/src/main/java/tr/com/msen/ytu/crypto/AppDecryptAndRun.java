package tr.com.msen.ytu.crypto;

import java.nio.file.Files;
import java.nio.file.Paths;

import iaik.pkcs.pkcs11.Module;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.Slot;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.objects.AESSecretKey;

public class AppDecryptAndRun {

	public static void main(String[] args) throws Exception {

		String libname = args[0];

		Module pkcs11Module = Module.getInstance(libname);
		pkcs11Module.initialize(null);

		Slot[] slots = pkcs11Module.getSlotList(Module.SlotRequirement.TOKEN_PRESENT);
		Slot selectedSlot = slots[0];
		Token token = selectedSlot.getToken();

		Session session = token.openSession(Token.SessionType.SERIAL_SESSION, Token.SessionReadWriteBehavior.RO_SESSION,
				null, null);

		session.login(Session.UserType.USER, args[1].toCharArray());

		AESSecretKey encryptionKey = FindSignatureKey.getSecretKey(session);
		System.out.println(encryptionKey);

		byte[] encryptedData = Files.readAllBytes(Paths.get("EncryptedApp"));
		byte[] decrypt = Decrypt.decrypt(session, encryptedData, encryptionKey);

		String path = TemporaryFile.createTempFile(decrypt);

		String processName = null;
		try {
			processName = RunAppFromJava.run(path);
		} catch (Exception e) {
			Files.deleteIfExists(Paths.get(path));
			return;
		}

		while (true) {
			try {
				session.generateRandom(1);
				Thread.sleep(2000l);
			} catch (Exception e) {
				RunAppFromJava.exit(processName);
				break;
			}
		}

		session.closeSession();
		pkcs11Module.finalize(null);
	}

}
