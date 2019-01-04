// Copyright (c) 2002 Graz University of Technology. All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
// 
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
// 
// 3. The end-user documentation included with the redistribution, if any, must
//    include the following acknowledgment:
// 
//    "This product includes software developed by IAIK of Graz University of
//     Technology."
// 
//    Alternately, this acknowledgment may appear in the software itself, if and
//    wherever such third-party acknowledgments normally appear.
// 
// 4. The names "Graz University of Technology" and "IAIK of Graz University of
//    Technology" must not be used to endorse or promote products derived from this
//    software without prior written permission.
// 
// 5. Products derived from this software may not be called "IAIK PKCS Wrapper",
//    nor may "IAIK" appear in their name, without prior written permission of
//    Graz University of Technology.
// 
// THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE LICENSOR BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
// OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
// ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
// OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package tr.com.msen.ytu.crypto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import iaik.pkcs.pkcs11.Mechanism;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.objects.AESSecretKey;
import iaik.pkcs.pkcs11.parameters.InitializationVectorParameters;
import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;

/**
 * This demo program uses a PKCS#11 module to encrypt a given file and test if
 * the data can be decrypted.
 */
public class Encrypt {

	public static void encrypt(Session session, String appFile) throws TokenException, IOException {
		Mechanism keyMechanism = Mechanism.get(PKCS11Constants.CKM_AES_KEY_GEN);
		AESSecretKey secretEncryptionKeyTemplate = new AESSecretKey();
		secretEncryptionKeyTemplate.getEncrypt().setBooleanValue(Boolean.TRUE);
		secretEncryptionKeyTemplate.getDecrypt().setBooleanValue(Boolean.TRUE);
		secretEncryptionKeyTemplate.getValueLen().setLongValue(new Long(16));

		AESSecretKey encryptionKey = (AESSecretKey) session.generateKey(keyMechanism, secretEncryptionKeyTemplate);

		AESSecretKey createObject = new AESSecretKey();
		createObject.getValue().setByteArrayValue(encryptionKey.getValue().getByteArrayValue());
		createObject.getToken().setBooleanValue(Boolean.TRUE);
		createObject.getPrivate().setBooleanValue(Boolean.TRUE);
		createObject.getEncrypt().setBooleanValue(Boolean.TRUE);
		createObject.getDecrypt().setBooleanValue(Boolean.TRUE);
		createObject.getLabel().setCharArrayValue("TestCert".toCharArray());

		session.createObject(createObject);
		
		
		byte[] rawData = Files.readAllBytes(Paths.get(appFile));

//		InputStream dataInputStream = new FileInputStream(appFile);
//
//		byte[] dataBuffer = new byte[1024];
//		int bytesRead;
//		ByteArrayOutputStream streamBuffer = new ByteArrayOutputStream();
//
//		// feed in all data from the input stream
//		while ((bytesRead = dataInputStream.read(dataBuffer)) >= 0) {
//			streamBuffer.write(dataBuffer, 0, bytesRead);
//		}
//		Arrays.fill(dataBuffer, (byte) 0); // ensure that no data is left in the
//											// memory
//		streamBuffer.flush();
//		streamBuffer.close();
//		byte[] rawData = streamBuffer.toByteArray();

		// be sure that your token can process the specified mechanism
		Mechanism encryptionMechanism = Mechanism.get(PKCS11Constants.CKM_AES_CBC_PAD);
		byte[] encryptInitializationVector = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		InitializationVectorParameters encryptInitializationVectorParameters = new InitializationVectorParameters(
				encryptInitializationVector);
		encryptionMechanism.setParameters(encryptInitializationVectorParameters);

		// initialize for encryption
		session.encryptInit(encryptionMechanism, encryptionKey);

		byte[] encryptedData = session.encrypt(rawData);

		Files.write(Paths.get("EncryptedApp"), encryptedData);
	}

}
