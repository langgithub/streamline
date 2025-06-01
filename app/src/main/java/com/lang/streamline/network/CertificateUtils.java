package com.lang.streamline.network;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class CertificateUtils {
    private static final String TAG = "CertUtils";

    /**
     * 将一张 X509Certificate 打印出它的主要属性
     */
    public static void dumpCertificate(X509Certificate cert) {
        if (cert == null) {
            Log.d(TAG, "certificate is null");
            return;
        }

        try {
            // 1. 基本信息
            Log.d(TAG, "=== Certificate Dump Begin ===");

            // 1.1 证书版本（通常是 “v3” 对应 integer = 3）
            int version = cert.getVersion();
            Log.d(TAG, "Version: V" + version);

            // 1.2 序列号（Serial Number）
            Log.d(TAG, "Serial Number: " + cert.getSerialNumber().toString(16));

            // 1.3 签名算法（Signature Algorithm）
            Log.d(TAG, "Signature Algorithm: " + cert.getSigAlgName());

            // 2. 主题与签发者
            // 2.1 主题（Subject），表示这张证书“是谁的”
            Log.d(TAG, "Subject DN: " + cert.getSubjectDN().getName());
            // 2.2 签发者（Issuer），表示是哪个 CA 签发的
            Log.d(TAG, "Issuer DN: " + cert.getIssuerDN().getName());

            // 3. 有效期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date notBefore = cert.getNotBefore();
            Date notAfter  = cert.getNotAfter();
            Log.d(TAG, "Not Before: " + sdf.format(notBefore));
            Log.d(TAG, "Not After : " + sdf.format(notAfter));

            // 4. 公钥信息
            // 4.1 获取公钥算法（e.g. RSA、EC 等）
            String pubAlg = cert.getPublicKey().getAlgorithm();
            Log.d(TAG, "Public Key Algorithm: " + pubAlg);
            // 4.2 获取公钥字节，Base64 编码打印（如果你想比对公钥哈希，就用这个字节做哈希）
            byte[] pubBytes = cert.getPublicKey().getEncoded();
            String pubKeyB64 = Base64.encodeToString(pubBytes, Base64.NO_WRAP);
            Log.d(TAG, "Public Key (Base64): " + pubKeyB64);

            // 5. Subject Alternative Names（可选）
            //    例如：DNS 名称、IP 地址等
            Collection<List<?>> altNames = cert.getSubjectAlternativeNames();
            if (altNames != null) {
                for (List<?> entry : altNames) {
                    Integer type = (Integer) entry.get(0);
                    Object value  = entry.get(1);
                    // type == 2 代表 DNS 名称，type == 7 代表 IP 地址
                    Log.d(TAG, "SubjectAltName (type=" + type + "): " + value);
                }
            }

            // 6. 扩展字段（Extensions），比如 BasicConstraints、KeyUsage、ExtendedKeyUsage 等
            // 6.1 Basic Constraints: 如果返回 ≥ 0，表示这是一个 CA 证书；否则返回 -1
            int pathLen = cert.getBasicConstraints();
            if (pathLen >= 0) {
                Log.d(TAG, "Basic Constraints: CA=true, pathLenConstraint=" + pathLen);
            } else {
                Log.d(TAG, "Basic Constraints: CA=false (End-entity cert)");
            }

            // 6.2 Key Usage: 一个 boolean[]，长度通常为 9
            boolean[] keyUsage = cert.getKeyUsage();
            if (keyUsage != null) {
                StringBuilder ku = new StringBuilder();
                // 常见索引说明：
                //  0: digitalSignature
                //  1: nonRepudiation
                //  2: keyEncipherment
                //  3: dataEncipherment
                //  4: keyAgreement
                //  5: keyCertSign
                //  6: cRLSign
                //  7: encipherOnly
                //  8: decipherOnly
                for (int i = 0; i < keyUsage.length; i++) {
                    if (keyUsage[i]) {
                        ku.append(i).append(",");
                    }
                }
                Log.d(TAG, "KeyUsage (indexes with 'true'): " + ku.toString());
            }

            // 6.3 Extended Key Usage: List of OID 字符串
            List<String> extKeyUsage = cert.getExtendedKeyUsage();
            if (extKeyUsage != null) {
                for (String oid : extKeyUsage) {
                    Log.d(TAG, "ExtendedKeyUsage OID: " + oid);
                }
            }

            // 7. 公开字段：序列化输出整个证书（PEM 格式）
            //    如果你想把整张证书看成文本，可以 Base64 编码好再加上头尾
            byte[] derBytes = cert.getEncoded(); // DER 格式字节
            StringBuilder pem = new StringBuilder();
            pem.append("-----BEGIN CERTIFICATE-----\n");
            pem.append(Base64.encodeToString(derBytes, Base64.NO_WRAP));
            pem.append("\n-----END CERTIFICATE-----");
            Log.d(TAG, "PEM Format Certificate:\n" + pem.toString());

            Log.d(TAG, "=== Certificate Dump End ===");
        } catch (Exception e) {
            Log.e(TAG, "Error while dumping certificate: " + e.getMessage(), e);
        }
    }
}
