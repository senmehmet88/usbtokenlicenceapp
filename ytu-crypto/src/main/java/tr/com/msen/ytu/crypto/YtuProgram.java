package tr.com.msen.ytu.crypto;

import java.io.IOException;

import iaik.pkcs.pkcs11.Module;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.TokenException;

public class YtuProgram {

	public static void main(String[] args) throws IOException, TokenException {

		String libname = "etpkcs11.dll";
		String tokenLabel = "Test123";
		String appFile = "test-ytu.bin";
		
		Module pkcs11Module = Module.getInstance(libname);
		pkcs11Module.initialize(null);
		
		
		Session session = InitializeToken.generateToken(pkcs11Module, tokenLabel);
		
		Encrypt.encrypt(session, appFile);
		
		session.closeSession();
		pkcs11Module.finalize(null);
	}

}
