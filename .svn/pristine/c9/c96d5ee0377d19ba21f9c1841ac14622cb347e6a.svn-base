package ru.alta.svd.protocol.test.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.alta.svd.protocol.test.dto.OperationResult;
import ru.alta.svd.protocol.test.dto.XMLFile;
import ru.alta.svd.protocol.test.dto.XMLFile.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Random;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author pechenko
 */
@Controller
public class SvdProtocolTestController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static final String TEST_FILE_ID = "123";
    private static final String TEST_FILE_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Envelope xmlns=\"http://www.w3.org/2001/06/soap-envelope\" xmlns:edh=\"urn:customs.ru:Envelope:EDHeader:2.0\"\n" +
            "          xmlns:att=\"urn:customs.ru:Envelope:Attachments:1.0\" xmlns:roi=\"urn:customs.ru:Envelope:RoutingInf:1.0\"\n" +
            "          xmlns:api=\"urn:customs.ru:Envelope:ApplicationInf:1.0\">\n" +
            "    <Header>\n" +
            "        <roi:RoutingInf>\n" +
            "            <roi:EnvelopeID>123B1CEE-59C1-4E09-A7DD-54C08E146C8B</roi:EnvelopeID>\n" +
            "            <roi:InitialEnvelopeID>123B1CEE-59C1-4E09-A7DD-54C08E146C8B</roi:InitialEnvelopeID>\n" +
            "            <roi:SenderInformation>smtp://eps.customs.ru/firm1</roi:SenderInformation>\n" +
            "            <roi:ReceiverInformation>smtp://eps.customs.ru/gateway</roi:ReceiverInformation>\n" +
            "            <roi:PreparationDateTime>2013-02-25T19:11:36+04:00</roi:PreparationDateTime>\n" +
            "        </roi:RoutingInf>\n" +
            "        <api:ApplicationInf>\n" +
            "            <api:SoftVersion>5.0.10/3.0.7</api:SoftVersion>\n" +
            "            <api:MessageKind>UD:10130172</api:MessageKind>\n" +
            "        </api:ApplicationInf>\n" +
            "        <edh:EDHeader>\n" +
            "            <edh:MessageType>CMN.13009</edh:MessageType>\n" +
            "            <edh:ProccessID>144fd294-68ca-4103-8b43-1c370a0b30db</edh:ProccessID>\n" +
            "            <edh:ParticipantID>1</edh:ParticipantID>\n" +
            "            <edh:ReceiverCustoms>\n" +
            "                <edh:CustomsCode>10130210</edh:CustomsCode>\n" +
            "                <edh:ExchType>19200</edh:ExchType>\n" +
            "            </edh:ReceiverCustoms>\n" +
            "        </edh:EDHeader>\n" +
            "    </Header>\n" +
            "    <Body>\n" +
            "        <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "            <SignedInfo>\n" +
            "                <CanonicalizationMethod Algorithm=\"urn:xml-dsig:transformation:v1.1\"/>\n" +
            "                <SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411\"/>\n" +
            "                <Reference URI=\"#KeyInfo\">\n" +
            "                    <Transforms>\n" +
            "                        <Transform Algorithm=\"urn:xml-dsig:transformation:v1.1\"/>\n" +
            "                    </Transforms>\n" +
            "                    <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/>\n" +
            "                    <DigestValue>IIsVea3BRBdTH2NzUJT9CJCKltxlp9om1EVxOm94Z7s=</DigestValue>\n" +
            "                </Reference>\n" +
            "                <Reference URI=\"#Object\">\n" +
            "                    <Transforms>\n" +
            "                        <Transform Algorithm=\"urn:xml-dsig:transformation:v1.1\"/>\n" +
            "                    </Transforms>\n" +
            "                    <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/>\n" +
            "                    <DigestValue>XzCR7xHlXTOvQzJuTsOgO6l9I6nP2FFsCuzqKxqUxXo=</DigestValue>\n" +
            "                </Reference>\n" +
            "            </SignedInfo>\n" +
            "            <SignatureValue>\n" +
            "                MIIDrgYJKoZIhvcNAQcCoIIDnzCCA5sCAQExDDAKBgYqhQMCAgkFADALBgkqhkiG9w0BBwExggN5MIIDdQIBATCCAScwggEXMSYwJAYJKoZIhvcNAQkBFhdBaG9yc292QG1haWwuY3VzdG9tcy5ydTELMAkGA1UEBhMCUlUxHTAbBgNVBAgeFAQcBD4EQQQ6BD4EMgRBBDoEMARPMRUwEwYDVQQHHgwEHAQ+BEEEOgQyBDAxSzBJBgNVBAoeQgQeBEIENAQ1BDsAIAQYBD0ERAQ+BEAEPAQwBEYEOAQ+BD0EPQQ+BDkAIAQRBDUENwQ+BD8EMARBBD0EPgRBBEIEODEpMCcGA1UECx4gBBMEHQQYBBIEJgAgBCQEIgQhACAEIAQ+BEEEQQQ4BDgxMjAwBgNVBAMTKVVkb3N0b3ZlcnlheXVzY2hpeSBDZW50ciBHTklWQyBGVFMgUm9zc2lpAgofoTX/AAAAAtOlMAoGBiqFAwICCQUAoIIB6TAYBgkqhkiG9w0BCQMxCwYJKoZIhvcNAQcBMBwGCSqGSIb3DQEJBTEPFw0xMzAyMjUxNTA4MzdaMC8GCSqGSIb3DQEJBDEiBCBv/vtS7Fq7/eILy3XL4jq3C109XJmfZAgbgsGT2qmBvDCCAXwGCyqGSIb3DQEJEAIvMYIBazCCAWcwggFjMIIBXzAIBgYqhQMCAgkEIE34+EYE4GfyUmjQDqMLdrjPPYmxMBpilY2aB6e/rVPOMIIBLzCCAR+kggEbMIIBFzEmMCQGCSqGSIb3DQEJARYXQWhvcnNvdkBtYWlsLmN1c3RvbXMucnUxCzAJBgNVBAYTAlJVMR0wGwYDVQQIHhQEHAQ+BEEEOgQ+BDIEQQQ6BDAETzEVMBMGA1UEBx4MBBwEPgRBBDoEMgQwMUswSQYDVQQKHkIEHgRCBDQENQQ7ACAEGAQ9BEQEPgRABDwEMARGBDgEPgQ9BD0EPgQ5ACAEEQQ1BDcEPgQ/BDAEQQQ9BD4EQQRCBDgxKTAnBgNVBAseIAQTBB0EGAQSBCYAIAQkBCIEIQAgBCAEPgRBBEEEOAQ4MTIwMAYDVQQDEylVZG9zdG92ZXJ5YXl1c2NoaXkgQ2VudHIgR05JVkMgRlRTIFJvc3NpaQIKH6E1/wAAAALTpTAKBgYqhQMCAhMFAARAWAz89IR8S5aZUeBm31KBxY30P+qvf0DUFG6z0vhO7DqNFXzaxaVwHfSuLxCRZebnv1yfvsB6qpJG+qfBzV8HEA==\n" +
            "            </SignatureValue>\n" +
            "            <KeyInfo Id=\"KeyInfo\">\n" +
            "                <X509Data>\n" +
            "                    <X509Certificate>\n" +
            "                        MIIEpzCCBFSgAwIBAgIKH6E1/wAAAALTpTAKBgYqhQMCAgMFADCCARcxJjAkBgkqhkiG9w0BCQEWF0Fob3Jzb3ZAbWFpbC5jdXN0b21zLnJ1MQswCQYDVQQGEwJSVTEdMBsGA1UECB4UBBwEPgRBBDoEPgQyBEEEOgQwBE8xFTATBgNVBAceDAQcBD4EQQQ6BDIEMDFLMEkGA1UECh5CBB4EQgQ0BDUEOwAgBBgEPQREBD4EQAQ8BDAERgQ4BD4EPQQ9BD4EOQAgBBEENQQ3BD4EPwQwBEEEPQQ+BEEEQgQ4MSkwJwYDVQQLHiAEEwQdBBgEEgQmACAEJAQiBCEAIAQgBD4EQQRBBDgEODEyMDAGA1UEAxMpVWRvc3RvdmVyeWF5dXNjaGl5IENlbnRyIEdOSVZDIEZUUyBSb3NzaWkwHhcNMTIwOTA3MDgyOTAwWhcNMTMwOTA3MDgzODAwWjCCATQxKjAoBgkqhkiG9w0BCQEWG0lyaW5hLkxhcG92b2tAbWVtby5pa2VhLmNvbTELMAkGA1UEBhMCUlUxDTALBgNVBAgeBAQcBB4xEzARBgNVBAceCgQlBDgEPAQ6BDgxIzAhBgNVBAoeGgQeBB4EHgAgBBgEGgQVBBAAIAQiBB4EIAQTMQ8wDQYDVQQLHgYEHgQiBBQxOTA3BgNVBAMeMAQbBDAEPwQ+BDIEPgQ6ACAEGARABDgEPQQwACAELQQ0BEMEMARABDQEPgQyBD0EMDEXMBUGA1UEBRMOMDA2LTI2OC04NTgtNTcxCjAIBgNVBAwTAS0xETAPBgNVBCseCAQYAC4ELQAuMRMwEQYDVQQqHgoEGARABDgEPQQwMRcwFQYDVQQEHg4EGwQwBD8EPgQyBD4EOjBjMBwGBiqFAwICEzASBgcqhQMCAiQABgcqhQMCAh4BA0MABEAqiXbOOmVFtV/9ppfSw3am1E+u4DiB9b0BeZXI++FkELmss6wJXqFiLGbjGB18xaikreO+gdpdGmfnKgxJLABao4IBWzCCAVcwDgYDVR0PAQH/BAQDAgTwMBkGCSqGSIb3DQEJDwQMMAowCAYGKoUDAgIVMCYGA1UdJQQfMB0GCCsGAQUFBwMEBgcqhQMCAiIGBggrBgEFBQcDAjAdBgNVHQ4EFgQUyrzSGPJxY7yEIPDtrNABwQsk8fcwHwYDVR0jBBgwFoAUAkTB4GGj7fwUIWza/AeUdTbwijUwgcEGA1UdHwSBuTCBtjBaoFigVoZUaHR0cDovLzEwLjIxLjE3LjY2L2ltYWdlcy9zdG9yaWVzL2ZpbGUvVWRvc3RvdmVyeWF5dXNjaGl5X0NlbnRyX0dOSVZDX0ZUU19Sb3NzaWkuY3JsMFigVqBUhlJodHRwOi8vY3VzdG9tcy5ydS9zcGVjaWFsL3NwX3NlcnZpY2UvVWRvc3RvdmVyeWF5dXNjaGl5X0NlbnRyX0dOSVZDX0ZUU19Sb3NzaWkuY3JsMAoGBiqFAwICAwUAA0EA0+5JqflREHZrcFU+gbtVMlsI4cM0KWtrbEyAEquX+oDdP7KKsVAIeqK7MaWPEuxJNMsHso1Tjh4cWzNf7Yrrbg==\n" +
            "                    </X509Certificate>\n" +
            "                </X509Data>\n" +
            "            </KeyInfo>\n" +
            "            <Object Id=\"Object\">\n" +
            "                <ED_Container DocumentModeID=\"1006058E\"\n" +
            "                              xmlns=\"urn:customs.ru:Information:ExchangeDocuments:ED_Container:5.0.8\"\n" +
            "                              xmlns:cat_ru=\"urn:customs.ru:CommonAggregateTypes:5.0.7\"\n" +
            "                              xmlns:clt_ru=\"urn:customs.ru:CommonLeafTypes:5.0.7\">\n" +
            "                    <cat_ru:DocumentID>48C10A40-2CDC-4E5F-A0F4-CEAE96084E8E</cat_ru:DocumentID>\n" +
            "                    <ContainerDoc>\n" +
            "                        <DocBody>\n" +
            "                            <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"\n" +
            "                                       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                                       xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "                                <SignedInfo>\n" +
            "                                    <CanonicalizationMethod Algorithm=\"urn:xml-dsig:transformation:v1.1\"/>\n" +
            "                                    <SignatureMethod\n" +
            "                                            Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411\"/>\n" +
            "                                    <Reference URI=\"#KeyInfo\">\n" +
            "                                        <Transforms>\n" +
            "                                            <Transform Algorithm=\"urn:xml-dsig:transformation:v1.1\"/>\n" +
            "                                        </Transforms>\n" +
            "                                        <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/>\n" +
            "                                        <DigestValue>IIsVea3BRBdTH2NzUJT9CJCKltxlp9om1EVxOm94Z7s=</DigestValue>\n" +
            "                                    </Reference>\n" +
            "                                    <Reference URI=\"#Object\">\n" +
            "                                        <Transforms>\n" +
            "                                            <Transform Algorithm=\"urn:xml-dsig:transformation:v1.1\"/>\n" +
            "                                        </Transforms>\n" +
            "                                        <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/>\n" +
            "                                        <DigestValue>tcECIZBqwAuhR+uwjaUaYr7nGFEy4oqAeSqB7+x1KHA=</DigestValue>\n" +
            "                                    </Reference>\n" +
            "                                </SignedInfo>\n" +
            "                                <SignatureValue>\n" +
            "                                    MIIDrgYJKoZIhvcNAQcCoIIDnzCCA5sCAQExDDAKBgYqhQMCAgkFADALBgkqhkiG9w0BBwExggN5MIIDdQIBATCCAScwggEXMSYwJAYJKoZIhvcNAQkBFhdBaG9yc292QG1haWwuY3VzdG9tcy5ydTELMAkGA1UEBhMCUlUxHTAbBgNVBAgeFAQcBD4EQQQ6BD4EMgRBBDoEMARPMRUwEwYDVQQHHgwEHAQ+BEEEOgQyBDAxSzBJBgNVBAoeQgQeBEIENAQ1BDsAIAQYBD0ERAQ+BEAEPAQwBEYEOAQ+BD0EPQQ+BDkAIAQRBDUENwQ+BD8EMARBBD0EPgRBBEIEODEpMCcGA1UECx4gBBMEHQQYBBIEJgAgBCQEIgQhACAEIAQ+BEEEQQQ4BDgxMjAwBgNVBAMTKVVkb3N0b3ZlcnlheXVzY2hpeSBDZW50ciBHTklWQyBGVFMgUm9zc2lpAgofoTX/AAAAAtOlMAoGBiqFAwICCQUAoIIB6TAYBgkqhkiG9w0BCQMxCwYJKoZIhvcNAQcBMBwGCSqGSIb3DQEJBTEPFw0xMzAyMjUxNTA4MzNaMC8GCSqGSIb3DQEJBDEiBCCqVY2XxgQgg9luNEhx2a8Yg+FJsc8RbneFJAIIbGjx4DCCAXwGCyqGSIb3DQEJEAIvMYIBazCCAWcwggFjMIIBXzAIBgYqhQMCAgkEIE34+EYE4GfyUmjQDqMLdrjPPYmxMBpilY2aB6e/rVPOMIIBLzCCAR+kggEbMIIBFzEmMCQGCSqGSIb3DQEJARYXQWhvcnNvdkBtYWlsLmN1c3RvbXMucnUxCzAJBgNVBAYTAlJVMR0wGwYDVQQIHhQEHAQ+BEEEOgQ+BDIEQQQ6BDAETzEVMBMGA1UEBx4MBBwEPgRBBDoEMgQwMUswSQYDVQQKHkIEHgRCBDQENQQ7ACAEGAQ9BEQEPgRABDwEMARGBDgEPgQ9BD0EPgQ5ACAEEQQ1BDcEPgQ/BDAEQQQ9BD4EQQRCBDgxKTAnBgNVBAseIAQTBB0EGAQSBCYAIAQkBCIEIQAgBCAEPgRBBEEEOAQ4MTIwMAYDVQQDEylVZG9zdG92ZXJ5YXl1c2NoaXkgQ2VudHIgR05JVkMgRlRTIFJvc3NpaQIKH6E1/wAAAALTpTAKBgYqhQMCAhMFAARAu7EXhlmn2XY0Zo8kOZeMuY/B9o0FeARM2GuYxD5JxvRijcbwqxwDkPcgO9biRcJKTJdLUw18e+3OxJlpCpr1+A==\n" +
            "                                </SignatureValue>\n" +
            "                                <KeyInfo Id=\"KeyInfo\">\n" +
            "                                    <X509Data>\n" +
            "                                        <X509Certificate>\n" +
            "                                            MIIEpzCCBFSgAwIBAgIKH6E1/wAAAALTpTAKBgYqhQMCAgMFADCCARcxJjAkBgkqhkiG9w0BCQEWF0Fob3Jzb3ZAbWFpbC5jdXN0b21zLnJ1MQswCQYDVQQGEwJSVTEdMBsGA1UECB4UBBwEPgRBBDoEPgQyBEEEOgQwBE8xFTATBgNVBAceDAQcBD4EQQQ6BDIEMDFLMEkGA1UECh5CBB4EQgQ0BDUEOwAgBBgEPQREBD4EQAQ8BDAERgQ4BD4EPQQ9BD4EOQAgBBEENQQ3BD4EPwQwBEEEPQQ+BEEEQgQ4MSkwJwYDVQQLHiAEEwQdBBgEEgQmACAEJAQiBCEAIAQgBD4EQQRBBDgEODEyMDAGA1UEAxMpVWRvc3RvdmVyeWF5dXNjaGl5IENlbnRyIEdOSVZDIEZUUyBSb3NzaWkwHhcNMTIwOTA3MDgyOTAwWhcNMTMwOTA3MDgzODAwWjCCATQxKjAoBgkqhkiG9w0BCQEWG0lyaW5hLkxhcG92b2tAbWVtby5pa2VhLmNvbTELMAkGA1UEBhMCUlUxDTALBgNVBAgeBAQcBB4xEzARBgNVBAceCgQlBDgEPAQ6BDgxIzAhBgNVBAoeGgQeBB4EHgAgBBgEGgQVBBAAIAQiBB4EIAQTMQ8wDQYDVQQLHgYEHgQiBBQxOTA3BgNVBAMeMAQbBDAEPwQ+BDIEPgQ6ACAEGARABDgEPQQwACAELQQ0BEMEMARABDQEPgQyBD0EMDEXMBUGA1UEBRMOMDA2LTI2OC04NTgtNTcxCjAIBgNVBAwTAS0xETAPBgNVBCseCAQYAC4ELQAuMRMwEQYDVQQqHgoEGARABDgEPQQwMRcwFQYDVQQEHg4EGwQwBD8EPgQyBD4EOjBjMBwGBiqFAwICEzASBgcqhQMCAiQABgcqhQMCAh4BA0MABEAqiXbOOmVFtV/9ppfSw3am1E+u4DiB9b0BeZXI++FkELmss6wJXqFiLGbjGB18xaikreO+gdpdGmfnKgxJLABao4IBWzCCAVcwDgYDVR0PAQH/BAQDAgTwMBkGCSqGSIb3DQEJDwQMMAowCAYGKoUDAgIVMCYGA1UdJQQfMB0GCCsGAQUFBwMEBgcqhQMCAiIGBggrBgEFBQcDAjAdBgNVHQ4EFgQUyrzSGPJxY7yEIPDtrNABwQsk8fcwHwYDVR0jBBgwFoAUAkTB4GGj7fwUIWza/AeUdTbwijUwgcEGA1UdHwSBuTCBtjBaoFigVoZUaHR0cDovLzEwLjIxLjE3LjY2L2ltYWdlcy9zdG9yaWVzL2ZpbGUvVWRvc3RvdmVyeWF5dXNjaGl5X0NlbnRyX0dOSVZDX0ZUU19Sb3NzaWkuY3JsMFigVqBUhlJodHRwOi8vY3VzdG9tcy5ydS9zcGVjaWFsL3NwX3NlcnZpY2UvVWRvc3RvdmVyeWF5dXNjaGl5X0NlbnRyX0dOSVZDX0ZUU19Sb3NzaWkuY3JsMAoGBiqFAwICAwUAA0EA0+5JqflREHZrcFU+gbtVMlsI4cM0KWtrbEyAEquX+oDdP7KKsVAIeqK7MaWPEuxJNMsHso1Tjh4cWzNf7Yrrbg==\n" +
            "                                        </X509Certificate>\n" +
            "                                    </X509Data>\n" +
            "                                </KeyInfo>\n" +
            "                                <Object Id=\"Object\">\n" +
            "                                    <ns0:DO1Report\n" +
            "                                            xmlns:ns6=\"urn:customs.ru:Information:WarehouseDocuments:DORegInfo:5.9.1\"\n" +
            "                                            xmlns:ns17=\"urn:customs.ru:CommonAggregateTypes:5.8.0\"\n" +
            "                                            xmlns:ns16=\"urn:customs.ru:Information:WarehouseDocuments:WarehouseCommonAggregateTypesCust:5.9.1\"\n" +
            "                                            xmlns:ns0=\"urn:customs.ru:Information:WarehouseDocuments:DO1Report:5.9.1\"\n" +
            "                                            DocumentModeID=\"1008001E\">\n" +
            "                                        <ns17:DocumentID>2D1E3755-1FB8-4EC1-9953-7858D95AB07F</ns17:DocumentID>\n" +
            "                                        <ns16:MainTransportModeCode>40</ns16:MainTransportModeCode>\n" +
            "                                        <ns16:ReportNumber>0086306</ns16:ReportNumber>\n" +
            "                                        <ns16:ReportDate>2018-11-08</ns16:ReportDate>\n" +
            "                                        <ns16:ReportTime>06:53:00</ns16:ReportTime>\n" +
            "                                        <ns16:WarehouseOwner>\n" +
            "                                            <ns17:OrganizationName>Общество с ограниченной ответственностью &quot;ДОМОДЕДОВО\n" +
            "                                                КАРГО&quot;\n" +
            "                                            </ns17:OrganizationName>\n" +
            "                                            <ns17:RFOrganizationFeatures>\n" +
            "                                                <ns17:INN>5009096881</ns17:INN>\n" +
            "                                                <ns17:KPP>500901001</ns17:KPP>\n" +
            "                                            </ns17:RFOrganizationFeatures>\n" +
            "                                            <ns16:Address>\n" +
            "                                                <ns16:AddressLine>142015 Московская обл.,г.Домодедово , территория\n" +
            "                                                    &quot;Аэропорт &quot;Домодедово&quot; СТР\n" +
            "                                                    7\n" +
            "                                                </ns16:AddressLine>\n" +
            "                                            </ns16:Address>\n" +
            "                                            <ns16:WarehouseLicense>\n" +
            "                                                <ns16:CertificateKind>lic_Certificate</ns16:CertificateKind>\n" +
            "                                                <ns16:CertificateNumber>10002/301210/10001/6</ns16:CertificateNumber>\n" +
            "                                                <ns16:CertificateDate>2015-02-02</ns16:CertificateDate>\n" +
            "                                            </ns16:WarehouseLicense>\n" +
            "                                            <ns16:WarehousePerson>\n" +
            "                                                <ns17:PersonSurname>Филинкова</ns17:PersonSurname>\n" +
            "                                                <ns17:PersonName>С. Г.</ns17:PersonName>\n" +
            "                                                <ns17:PersonPost>Специалист по таможенной отчетности</ns17:PersonPost>\n" +
            "                                            </ns16:WarehousePerson>\n" +
            "                                        </ns16:WarehouseOwner>\n" +
            "                                        <ns0:Carrier>\n" +
            "                                            <ns17:OrganizationName>Swiss International Air Lines Ltd.\n" +
            "                                            </ns17:OrganizationName>\n" +
            "                                            <ns16:CountryCode>756</ns16:CountryCode>\n" +
            "                                            <ns16:Address>\n" +
            "                                                <ns17:CountryCode>CH</ns17:CountryCode>\n" +
            "                                                <ns17:CounryName>ШВЕЙЦАРИЯ</ns17:CounryName>\n" +
            "                                                <ns16:AddressLine>Нет данных</ns16:AddressLine>\n" +
            "                                            </ns16:Address>\n" +
            "                                        </ns0:Carrier>\n" +
            "                                        <ns0:Transports>\n" +
            "                                            <ns16:TransportModeCode>40</ns16:TransportModeCode>\n" +
            "                                            <ns16:TransportIdentifier>HBIJN</ns16:TransportIdentifier>\n" +
            "                                            <ns16:Avia>\n" +
            "                                                <ns16:FlightNumber>LX1324</ns16:FlightNumber>\n" +
            "                                                <ns16:FlightDate>2018-11-08</ns16:FlightDate>\n" +
            "                                            </ns16:Avia>\n" +
            "                                        </ns0:Transports>\n" +
            "                                        <ns0:GoodsShipment>\n" +
            "                                            <ns0:TotalPackageNumber>1</ns0:TotalPackageNumber>\n" +
            "                                            <ns0:MPOSign>false</ns0:MPOSign>\n" +
            "                                            <ns0:TransportDocs>\n" +
            "                                                <ns17:PrDocumentName>Авианакладная</ns17:PrDocumentName>\n" +
            "                                                <ns17:PrDocumentNumber>724-69438832</ns17:PrDocumentNumber>\n" +
            "                                                <ns17:PrDocumentDate>2018-11-06</ns17:PrDocumentDate>\n" +
            "                                                <ns16:PresentedDocumentModeCode>02017</ns16:PresentedDocumentModeCode>\n" +
            "                                                <ns16:Consignor>\n" +
            "                                                    <ns16:OrganizationName>A J WALTER AVIATION THE H\n" +
            "                                                    </ns16:OrganizationName>\n" +
            "                                                    <ns16:Address>\n" +
            "                                                        <ns17:CountryCode>00</ns17:CountryCode>\n" +
            "                                                        <ns17:CounryName>НЕИЗВЕСТНА</ns17:CounryName>\n" +
            "                                                        <ns16:AddressLine>A J WALTER AVIATION THE H</ns16:AddressLine>\n" +
            "                                                    </ns16:Address>\n" +
            "                                                </ns16:Consignor>\n" +
            "                                                <ns16:Consignee>\n" +
            "                                                    <ns17:OrganizationName>&quot;JSC SIBERIA AIRLINES&quot;\n" +
            "                                                    </ns17:OrganizationName>\n" +
            "                                                    <ns16:Address>\n" +
            "                                                        <ns17:CountryCode>RU</ns17:CountryCode>\n" +
            "                                                        <ns17:CounryName>РОССИЯ</ns17:CounryName>\n" +
            "                                                        <ns16:AddressLine>JSC SIBERIA AIRLINES, 142015, г.DOMODEDOVO\n" +
            "                                                            AIRPORT MOSCOW AREA\n" +
            "                                                        </ns16:AddressLine>\n" +
            "                                                    </ns16:Address>\n" +
            "                                                </ns16:Consignee>\n" +
            "                                                <ns16:Goods>\n" +
            "                                                    <ns16:GoodsNumber>1</ns16:GoodsNumber>\n" +
            "                                                    <ns16:GoodsDescription>авиазапчасти</ns16:GoodsDescription>\n" +
            "                                                    <ns16:CargoPlace>\n" +
            "                                                        <ns16:PlaceNumber>1</ns16:PlaceNumber>\n" +
            "                                                    </ns16:CargoPlace>\n" +
            "                                                    <ns16:BruttoVolQuant>\n" +
            "                                                        <ns16:GoodsQuantity>7.2</ns16:GoodsQuantity>\n" +
            "                                                        <ns16:MeasureUnitQualifierName>КГ\n" +
            "                                                        </ns16:MeasureUnitQualifierName>\n" +
            "                                                        <ns16:MeasureUnitQualifierCode>166\n" +
            "                                                        </ns16:MeasureUnitQualifierCode>\n" +
            "                                                    </ns16:BruttoVolQuant>\n" +
            "                                                    <ns16:GoodsWHNumber>СВХnew-06-06-03 - 07:12</ns16:GoodsWHNumber>\n" +
            "                                                    <ns16:KeepingLimit>\n" +
            "                                                        <ns16:AcceptDate>2018-11-08</ns16:AcceptDate>\n" +
            "                                                        <ns16:AcceptTime>06:09:00</ns16:AcceptTime>\n" +
            "                                                        <ns16:DeadLineDate>2019-03-09</ns16:DeadLineDate>\n" +
            "                                                    </ns16:KeepingLimit>\n" +
            "                                                </ns16:Goods>\n" +
            "                                            </ns0:TransportDocs>\n" +
            "                                        </ns0:GoodsShipment>\n" +
            "                                    </ns0:DO1Report>\n" +
            "                                </Object>\n" +
            "                            </Signature>\n" +
            "                        </DocBody>\n" +
            "                    </ContainerDoc>\n" +
            "                    <ContainerDoc>\n" +
            "                        <DocBody>\n" +
            "                            <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"\n" +
            "                                       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                                       xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "                                <SignedInfo>\n" +
            "                                    <CanonicalizationMethod Algorithm=\"urn:xml-dsig:transformation:v1.1\"/>\n" +
            "                                    <SignatureMethod\n" +
            "                                            Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411\"/>\n" +
            "                                    <Reference URI=\"#KeyInfo\">\n" +
            "                                        <Transforms>\n" +
            "                                            <Transform Algorithm=\"urn:xml-dsig:transformation:v1.1\"/>\n" +
            "                                        </Transforms>\n" +
            "                                        <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/>\n" +
            "                                        <DigestValue>IIsVea3BRBdTH2NzUJT9CJCKltxlp9om1EVxOm94Z7s=</DigestValue>\n" +
            "                                    </Reference>\n" +
            "                                    <Reference URI=\"#Object\">\n" +
            "                                        <Transforms>\n" +
            "                                            <Transform Algorithm=\"urn:xml-dsig:transformation:v1.1\"/>\n" +
            "                                        </Transforms>\n" +
            "                                        <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/>\n" +
            "                                        <DigestValue>2H6cGUOo/OT1DEmxMnjM6Bp0Jawzz8V66Hr4xy0Hoho=</DigestValue>\n" +
            "                                    </Reference>\n" +
            "                                </SignedInfo>\n" +
            "                                <SignatureValue>\n" +
            "                                    MIIDrgYJKoZIhvcNAQcCoIIDnzCCA5sCAQExDDAKBgYqhQMCAgkFADALBgkqhkiG9w0BBwExggN5MIIDdQIBATCCAScwggEXMSYwJAYJKoZIhvcNAQkBFhdBaG9yc292QG1haWwuY3VzdG9tcy5ydTELMAkGA1UEBhMCUlUxHTAbBgNVBAgeFAQcBD4EQQQ6BD4EMgRBBDoEMARPMRUwEwYDVQQHHgwEHAQ+BEEEOgQyBDAxSzBJBgNVBAoeQgQeBEIENAQ1BDsAIAQYBD0ERAQ+BEAEPAQwBEYEOAQ+BD0EPQQ+BDkAIAQRBDUENwQ+BD8EMARBBD0EPgRBBEIEODEpMCcGA1UECx4gBBMEHQQYBBIEJgAgBCQEIgQhACAEIAQ+BEEEQQQ4BDgxMjAwBgNVBAMTKVVkb3N0b3ZlcnlheXVzY2hpeSBDZW50ciBHTklWQyBGVFMgUm9zc2lpAgofoTX/AAAAAtOlMAoGBiqFAwICCQUAoIIB6TAYBgkqhkiG9w0BCQMxCwYJKoZIhvcNAQcBMBwGCSqGSIb3DQEJBTEPFw0xMzAyMjUxNTA4MzRaMC8GCSqGSIb3DQEJBDEiBCCbxzkXofs8SBKreyC7p5SYeYPPGmwW6iavgUpSEA3bizCCAXwGCyqGSIb3DQEJEAIvMYIBazCCAWcwggFjMIIBXzAIBgYqhQMCAgkEIE34+EYE4GfyUmjQDqMLdrjPPYmxMBpilY2aB6e/rVPOMIIBLzCCAR+kggEbMIIBFzEmMCQGCSqGSIb3DQEJARYXQWhvcnNvdkBtYWlsLmN1c3RvbXMucnUxCzAJBgNVBAYTAlJVMR0wGwYDVQQIHhQEHAQ+BEEEOgQ+BDIEQQQ6BDAETzEVMBMGA1UEBx4MBBwEPgRBBDoEMgQwMUswSQYDVQQKHkIEHgRCBDQENQQ7ACAEGAQ9BEQEPgRABDwEMARGBDgEPgQ9BD0EPgQ5ACAEEQQ1BDcEPgQ/BDAEQQQ9BD4EQQRCBDgxKTAnBgNVBAseIAQTBB0EGAQSBCYAIAQkBCIEIQAgBCAEPgRBBEEEOAQ4MTIwMAYDVQQDEylVZG9zdG92ZXJ5YXl1c2NoaXkgQ2VudHIgR05JVkMgRlRTIFJvc3NpaQIKH6E1/wAAAALTpTAKBgYqhQMCAhMFAARA9OhMugbAvIpwZgJiK3HvvfkEQV8XYrNg9jG/P6ZK7H7SM5a3LZjhCA2Wbo4F8HMTQZDz6jLrl7pcLn1LZqtWfA==\n" +
            "                                </SignatureValue>\n" +
            "                                <KeyInfo Id=\"KeyInfo\">\n" +
            "                                    <X509Data>\n" +
            "                                        <X509Certificate>\n" +
            "                                            MIIEpzCCBFSgAwIBAgIKH6E1/wAAAALTpTAKBgYqhQMCAgMFADCCARcxJjAkBgkqhkiG9w0BCQEWF0Fob3Jzb3ZAbWFpbC5jdXN0b21zLnJ1MQswCQYDVQQGEwJSVTEdMBsGA1UECB4UBBwEPgRBBDoEPgQyBEEEOgQwBE8xFTATBgNVBAceDAQcBD4EQQQ6BDIEMDFLMEkGA1UECh5CBB4EQgQ0BDUEOwAgBBgEPQREBD4EQAQ8BDAERgQ4BD4EPQQ9BD4EOQAgBBEENQQ3BD4EPwQwBEEEPQQ+BEEEQgQ4MSkwJwYDVQQLHiAEEwQdBBgEEgQmACAEJAQiBCEAIAQgBD4EQQRBBDgEODEyMDAGA1UEAxMpVWRvc3RvdmVyeWF5dXNjaGl5IENlbnRyIEdOSVZDIEZUUyBSb3NzaWkwHhcNMTIwOTA3MDgyOTAwWhcNMTMwOTA3MDgzODAwWjCCATQxKjAoBgkqhkiG9w0BCQEWG0lyaW5hLkxhcG92b2tAbWVtby5pa2VhLmNvbTELMAkGA1UEBhMCUlUxDTALBgNVBAgeBAQcBB4xEzARBgNVBAceCgQlBDgEPAQ6BDgxIzAhBgNVBAoeGgQeBB4EHgAgBBgEGgQVBBAAIAQiBB4EIAQTMQ8wDQYDVQQLHgYEHgQiBBQxOTA3BgNVBAMeMAQbBDAEPwQ+BDIEPgQ6ACAEGARABDgEPQQwACAELQQ0BEMEMARABDQEPgQyBD0EMDEXMBUGA1UEBRMOMDA2LTI2OC04NTgtNTcxCjAIBgNVBAwTAS0xETAPBgNVBCseCAQYAC4ELQAuMRMwEQYDVQQqHgoEGARABDgEPQQwMRcwFQYDVQQEHg4EGwQwBD8EPgQyBD4EOjBjMBwGBiqFAwICEzASBgcqhQMCAiQABgcqhQMCAh4BA0MABEAqiXbOOmVFtV/9ppfSw3am1E+u4DiB9b0BeZXI++FkELmss6wJXqFiLGbjGB18xaikreO+gdpdGmfnKgxJLABao4IBWzCCAVcwDgYDVR0PAQH/BAQDAgTwMBkGCSqGSIb3DQEJDwQMMAowCAYGKoUDAgIVMCYGA1UdJQQfMB0GCCsGAQUFBwMEBgcqhQMCAiIGBggrBgEFBQcDAjAdBgNVHQ4EFgQUyrzSGPJxY7yEIPDtrNABwQsk8fcwHwYDVR0jBBgwFoAUAkTB4GGj7fwUIWza/AeUdTbwijUwgcEGA1UdHwSBuTCBtjBaoFigVoZUaHR0cDovLzEwLjIxLjE3LjY2L2ltYWdlcy9zdG9yaWVzL2ZpbGUvVWRvc3RvdmVyeWF5dXNjaGl5X0NlbnRyX0dOSVZDX0ZUU19Sb3NzaWkuY3JsMFigVqBUhlJodHRwOi8vY3VzdG9tcy5ydS9zcGVjaWFsL3NwX3NlcnZpY2UvVWRvc3RvdmVyeWF5dXNjaGl5X0NlbnRyX0dOSVZDX0ZUU19Sb3NzaWkuY3JsMAoGBiqFAwICAwUAA0EA0+5JqflREHZrcFU+gbtVMlsI4cM0KWtrbEyAEquX+oDdP7KKsVAIeqK7MaWPEuxJNMsHso1Tjh4cWzNf7Yrrbg==\n" +
            "                                        </X509Certificate>\n" +
            "                                    </X509Data>\n" +
            "                                </KeyInfo>\n" +
            "                                <Object Id=\"Object\">\n" +
            "                                    <ns2:WHGoodOut xmlns:ns17=\"urn:customs.ru:CommonAggregateTypes:5.8.0\"\n" +
            "                                                   xmlns:ns16=\"urn:customs.ru:Information:WarehouseDocuments:WarehouseCommonAggregateTypesCust:5.9.1\"\n" +
            "                                                   xmlns:ns2=\"urn:customs.ru:Information:WarehouseDocuments:WHGoodOut:5.9.1\"\n" +
            "                                                   DocumentModeID=\"1008021E\">\n" +
            "                                        <ns17:DocumentID>26D97FC9-7924-49FD-9059-78B2BAF24811</ns17:DocumentID>\n" +
            "                                        <ns2:DocumentKind>GoodOutDecision</ns2:DocumentKind>\n" +
            "                                        <ns2:SendDate>2019-02-08</ns2:SendDate>\n" +
            "                                        <ns2:SendTime>19:26:15.954</ns2:SendTime>\n" +
            "                                        <ns2:ReleaseDate>2019-02-08</ns2:ReleaseDate>\n" +
            "                                        <ns2:Comments>Установлена связь ДТ 10702070/070219/0023698 со следующими\n" +
            "                                            документами ВХ: 1. Пред-е Рег.№ 10702030/030219/000104 по совпадению [02011]\n" +
            "                                            КОНОСАМЕНТ SNKO024190101289 от 30.01.2019 2. ДО-1 Рег.№\n" +
            "                                            10702030/040219/0018553 по совпадению [02011] КОНОСАМЕНТ S\n" +
            "                                        </ns2:Comments>\n" +
            "                                        <ns2:Customs>\n" +
            "                                            <ns17:Code>10702030</ns17:Code>\n" +
            "                                            <ns17:OfficeName>т/п Морской порт Владивосток</ns17:OfficeName>\n" +
            "                                        </ns2:Customs>\n" +
            "                                        <ns2:CustomsPerson>\n" +
            "                                            <ns17:PersonName>Сервис Процедур ЭД (v.101)</ns17:PersonName>\n" +
            "                                            <ns17:LNP>000</ns17:LNP>\n" +
            "                                        </ns2:CustomsPerson>\n" +
            "                                        <ns2:DeliveryGoods>\n" +
            "                                            <ns2:Transport>\n" +
            "                                                <ns16:TransportModeCode>10</ns16:TransportModeCode>\n" +
            "                                                <ns16:TransportIdentifier>NAGOYA TRADER 19001W\n" +
            "                                                </ns16:TransportIdentifier>\n" +
            "                                            </ns2:Transport>\n" +
            "                                            <ns2:GoodInfo>\n" +
            "                                                <ns16:GoodsNumber>1</ns16:GoodsNumber>\n" +
            "                                                <ns16:GoodsTNVEDCode>7318290009</ns16:GoodsTNVEDCode>\n" +
            "                                                <ns16:InvoiceCost>111278.00</ns16:InvoiceCost>\n" +
            "                                                <ns16:CurrencyCode>CNY</ns16:CurrencyCode>\n" +
            "                                                <ns16:GoodsDescription>КРЕПЕЖНЫЕ ИЗДЕЛИЯ ИЗ ЧЕРНЫХ МЕТАЛОВ, БЕЗ РЕЗЬБЫ,\n" +
            "                                                    ШТИФТ ДЛЯ ФИКАСАЦИИ ОПАЛУБКИ, ИСПОЛЬЗУЮТСЯ КАК КРЕПЕЖНЫЕ\n" +
            "                                                    СОЕДИНИТЕЛЬНЫЕ ЭЛЕМЕНТЫ УСТАНАВЛИВАЕМЫЕ В СПЕЦИАЛЬНЫЕ\n" +
            "                                                    ТЕХНОЛОГИЧЕСКИЕ ОТВЕРСТИЯ КОНСТРУКТИВНО ПРЕДУСМОТРЕННЫЕ НА БЕТОННОЙ\n" +
            "                                                    ОПАЛУБКЕ ПРИМЕНЯЕМОЙ В\n" +
            "                                                </ns16:GoodsDescription>\n" +
            "                                                <ns16:GoodsDescription>СТРОИТЕЛЬСТВЕ.</ns16:GoodsDescription>\n" +
            "                                                <ns16:CargoPlace>\n" +
            "                                                    <ns16:PlaceNumber>6</ns16:PlaceNumber>\n" +
            "                                                </ns16:CargoPlace>\n" +
            "                                                <ns16:BruttoVolQuant>\n" +
            "                                                    <ns16:GoodsQuantity>5700.000000</ns16:GoodsQuantity>\n" +
            "                                                    <ns16:MeasureUnitQualifierName>КГ</ns16:MeasureUnitQualifierName>\n" +
            "                                                    <ns16:MeasureUnitQualifierCode>166</ns16:MeasureUnitQualifierCode>\n" +
            "                                                </ns16:BruttoVolQuant>\n" +
            "                                                <ns2:Container>\n" +
            "                                                    <ns16:ContainerNumber>SKLU1658364</ns16:ContainerNumber>\n" +
            "                                                </ns2:Container>\n" +
            "                                                <ns2:TransportDoc>\n" +
            "                                                    <ns17:PrDocumentName>КОНОСАМЕНТ</ns17:PrDocumentName>\n" +
            "                                                    <ns17:PrDocumentNumber>SNKO024190101289</ns17:PrDocumentNumber>\n" +
            "                                                    <ns17:PrDocumentDate>2019-01-30</ns17:PrDocumentDate>\n" +
            "                                                </ns2:TransportDoc>\n" +
            "                                                <ns2:GoodsGroupDescription>\n" +
            "                                                    <ns2:GoodsDescription>:ВСЕГО 100000 ШТ.</ns2:GoodsDescription>\n" +
            "                                                    <ns2:GoodsGroupInformation>\n" +
            "                                                        <ns2:Manufacturer>QINGDAO XINGHE MACHINERY CO.,LTD\n" +
            "                                                        </ns2:Manufacturer>\n" +
            "                                                        <ns2:TradeMark>НЕ ОБОЗНАЧЕН</ns2:TradeMark>\n" +
            "                                                    </ns2:GoodsGroupInformation>\n" +
            "                                                </ns2:GoodsGroupDescription>\n" +
            "                                            </ns2:GoodInfo>\n" +
            "                                        </ns2:DeliveryGoods>\n" +
            "                                        <ns2:TotalPackageNumber>6</ns2:TotalPackageNumber>\n" +
            "                                        <ns2:ProduceDocuments>\n" +
            "                                            <ns17:PrDocumentName>ДТ</ns17:PrDocumentName>\n" +
            "                                            <ns17:PrDocumentNumber>10702070/070219/0023698</ns17:PrDocumentNumber>\n" +
            "                                            <ns17:PrDocumentDate>2019-02-07</ns17:PrDocumentDate>\n" +
            "                                        </ns2:ProduceDocuments>\n" +
            "                                        <ns2:SVHLicenceNumber>\n" +
            "                                            <ns17:PrDocumentNumber>10702/211218/00065/6</ns17:PrDocumentNumber>\n" +
            "                                            <ns17:PrDocumentDate>2018-12-22</ns17:PrDocumentDate>\n" +
            "                                        </ns2:SVHLicenceNumber>\n" +
            "                                    </ns2:WHGoodOut>\n" +
            "                                </Object>\n" +
            "                            </Signature>\n" +
            "                        </DocBody>\n" +
            "                    </ContainerDoc>\n" +
            "                    <ContainerDoc>\n" +
            "                        <DocBody>\n" +
            "                            <Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"\n" +
            "                                       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                                       xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "                                <SignedInfo>\n" +
            "                                    <CanonicalizationMethod Algorithm=\"urn:xml-dsig:transformation:v1.1\"/>\n" +
            "                                    <SignatureMethod\n" +
            "                                            Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411\"/>\n" +
            "                                    <Reference URI=\"#KeyInfo\">\n" +
            "                                        <Transforms>\n" +
            "                                            <Transform Algorithm=\"urn:xml-dsig:transformation:v1.1\"/>\n" +
            "                                        </Transforms>\n" +
            "                                        <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/>\n" +
            "                                        <DigestValue>IIsVea3BRBdTH2NzUJT9CJCKltxlp9om1EVxOm94Z7s=</DigestValue>\n" +
            "                                    </Reference>\n" +
            "                                    <Reference URI=\"#Object\">\n" +
            "                                        <Transforms>\n" +
            "                                            <Transform Algorithm=\"urn:xml-dsig:transformation:v1.1\"/>\n" +
            "                                        </Transforms>\n" +
            "                                        <DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gostr3411\"/>\n" +
            "                                        <DigestValue>Qhiu4325Dq7cvSmcYmboZvFOlKHKCddz50zfQgywets=</DigestValue>\n" +
            "                                    </Reference>\n" +
            "                                </SignedInfo>\n" +
            "                                <SignatureValue>\n" +
            "                                    MIIDrgYJKoZIhvcNAQcCoIIDnzCCA5sCAQExDDAKBgYqhQMCAgkFADALBgkqhkiG9w0BBwExggN5MIIDdQIBATCCAScwggEXMSYwJAYJKoZIhvcNAQkBFhdBaG9yc292QG1haWwuY3VzdG9tcy5ydTELMAkGA1UEBhMCUlUxHTAbBgNVBAgeFAQcBD4EQQQ6BD4EMgRBBDoEMARPMRUwEwYDVQQHHgwEHAQ+BEEEOgQyBDAxSzBJBgNVBAoeQgQeBEIENAQ1BDsAIAQYBD0ERAQ+BEAEPAQwBEYEOAQ+BD0EPQQ+BDkAIAQRBDUENwQ+BD8EMARBBD0EPgRBBEIEODEpMCcGA1UECx4gBBMEHQQYBBIEJgAgBCQEIgQhACAEIAQ+BEEEQQQ4BDgxMjAwBgNVBAMTKVVkb3N0b3ZlcnlheXVzY2hpeSBDZW50ciBHTklWQyBGVFMgUm9zc2lpAgofoTX/AAAAAtOlMAoGBiqFAwICCQUAoIIB6TAYBgkqhkiG9w0BCQMxCwYJKoZIhvcNAQcBMBwGCSqGSIb3DQEJBTEPFw0xMzAyMjUxNTA4MzVaMC8GCSqGSIb3DQEJBDEiBCBVtOJ5Wn23uz+PpanUBgyMCiB/4nRCB8KdvTQAom8Z6TCCAXwGCyqGSIb3DQEJEAIvMYIBazCCAWcwggFjMIIBXzAIBgYqhQMCAgkEIE34+EYE4GfyUmjQDqMLdrjPPYmxMBpilY2aB6e/rVPOMIIBLzCCAR+kggEbMIIBFzEmMCQGCSqGSIb3DQEJARYXQWhvcnNvdkBtYWlsLmN1c3RvbXMucnUxCzAJBgNVBAYTAlJVMR0wGwYDVQQIHhQEHAQ+BEEEOgQ+BDIEQQQ6BDAETzEVMBMGA1UEBx4MBBwEPgRBBDoEMgQwMUswSQYDVQQKHkIEHgRCBDQENQQ7ACAEGAQ9BEQEPgRABDwEMARGBDgEPgQ9BD0EPgQ5ACAEEQQ1BDcEPgQ/BDAEQQQ9BD4EQQRCBDgxKTAnBgNVBAseIAQTBB0EGAQSBCYAIAQkBCIEIQAgBCAEPgRBBEEEOAQ4MTIwMAYDVQQDEylVZG9zdG92ZXJ5YXl1c2NoaXkgQ2VudHIgR05JVkMgRlRTIFJvc3NpaQIKH6E1/wAAAALTpTAKBgYqhQMCAhMFAARAtxR1j586l4kjzJ2V0eokLVWDzN7M1HYYXdJ4U+puWmwYFZ52krQ5jgP+5BdkS8WfE1S6i9VQKiThuAdu2iasxg==\n" +
            "                                </SignatureValue>\n" +
            "                                <KeyInfo Id=\"KeyInfo\">\n" +
            "                                    <X509Data>\n" +
            "                                        <X509Certificate>\n" +
            "                                            MIIEpzCCBFSgAwIBAgIKH6E1/wAAAALTpTAKBgYqhQMCAgMFADCCARcxJjAkBgkqhkiG9w0BCQEWF0Fob3Jzb3ZAbWFpbC5jdXN0b21zLnJ1MQswCQYDVQQGEwJSVTEdMBsGA1UECB4UBBwEPgRBBDoEPgQyBEEEOgQwBE8xFTATBgNVBAceDAQcBD4EQQQ6BDIEMDFLMEkGA1UECh5CBB4EQgQ0BDUEOwAgBBgEPQREBD4EQAQ8BDAERgQ4BD4EPQQ9BD4EOQAgBBEENQQ3BD4EPwQwBEEEPQQ+BEEEQgQ4MSkwJwYDVQQLHiAEEwQdBBgEEgQmACAEJAQiBCEAIAQgBD4EQQRBBDgEODEyMDAGA1UEAxMpVWRvc3RvdmVyeWF5dXNjaGl5IENlbnRyIEdOSVZDIEZUUyBSb3NzaWkwHhcNMTIwOTA3MDgyOTAwWhcNMTMwOTA3MDgzODAwWjCCATQxKjAoBgkqhkiG9w0BCQEWG0lyaW5hLkxhcG92b2tAbWVtby5pa2VhLmNvbTELMAkGA1UEBhMCUlUxDTALBgNVBAgeBAQcBB4xEzARBgNVBAceCgQlBDgEPAQ6BDgxIzAhBgNVBAoeGgQeBB4EHgAgBBgEGgQVBBAAIAQiBB4EIAQTMQ8wDQYDVQQLHgYEHgQiBBQxOTA3BgNVBAMeMAQbBDAEPwQ+BDIEPgQ6ACAEGARABDgEPQQwACAELQQ0BEMEMARABDQEPgQyBD0EMDEXMBUGA1UEBRMOMDA2LTI2OC04NTgtNTcxCjAIBgNVBAwTAS0xETAPBgNVBCseCAQYAC4ELQAuMRMwEQYDVQQqHgoEGARABDgEPQQwMRcwFQYDVQQEHg4EGwQwBD8EPgQyBD4EOjBjMBwGBiqFAwICEzASBgcqhQMCAiQABgcqhQMCAh4BA0MABEAqiXbOOmVFtV/9ppfSw3am1E+u4DiB9b0BeZXI++FkELmss6wJXqFiLGbjGB18xaikreO+gdpdGmfnKgxJLABao4IBWzCCAVcwDgYDVR0PAQH/BAQDAgTwMBkGCSqGSIb3DQEJDwQMMAowCAYGKoUDAgIVMCYGA1UdJQQfMB0GCCsGAQUFBwMEBgcqhQMCAiIGBggrBgEFBQcDAjAdBgNVHQ4EFgQUyrzSGPJxY7yEIPDtrNABwQsk8fcwHwYDVR0jBBgwFoAUAkTB4GGj7fwUIWza/AeUdTbwijUwgcEGA1UdHwSBuTCBtjBaoFigVoZUaHR0cDovLzEwLjIxLjE3LjY2L2ltYWdlcy9zdG9yaWVzL2ZpbGUvVWRvc3RvdmVyeWF5dXNjaGl5X0NlbnRyX0dOSVZDX0ZUU19Sb3NzaWkuY3JsMFigVqBUhlJodHRwOi8vY3VzdG9tcy5ydS9zcGVjaWFsL3NwX3NlcnZpY2UvVWRvc3RvdmVyeWF5dXNjaGl5X0NlbnRyX0dOSVZDX0ZUU19Sb3NzaWkuY3JsMAoGBiqFAwICAwUAA0EA0+5JqflREHZrcFU+gbtVMlsI4cM0KWtrbEyAEquX+oDdP7KKsVAIeqK7MaWPEuxJNMsHso1Tjh4cWzNf7Yrrbg==\n" +
            "                                        </X509Certificate>\n" +
            "                                    </X509Data>\n" +
            "                                </KeyInfo>\n" +
            "                                <Object Id=\"Object\">\n" +
            "                                    <ns0:DORegInfo  xmlns:ns6=\"urn:customs.ru:Information:WarehouseDocuments:DORegInfo:5.9.1\"\n" +
            "                                                xmlns:ns17=\"urn:customs.ru:CommonAggregateTypes:5.8.0\"\n" +
            "                                                xmlns:ns16=\"urn:customs.ru:Information:WarehouseDocuments:WarehouseCommonAggregateTypesCust:5.9.1\"\n" +
            "                                                xmlns:ns0=\"urn:customs.ru:Information:WarehouseDocuments:DO1Report:5.9.1\"\n" +
            "                                                DocumentModeID=\"1008001E\">\n" +
            "                                        <cat_ru:DocumentID>48C10A40-2CDC-4E5F-A0F4-CEAE96084E8E</cat_ru:DocumentID>\n" +
            "                                        <cat_ru:RefDocumentID>48C10A40-2CDC-4E5F-A0F4-CEAE96084E8E</cat_ru:RefDocumentID>\n" +
            "                                        <RegDate>2019-01-27</RegDate>\n" +
            "                                        <RegTime>10:10:10</RegTime>\n" +
            "                                        <FormReport>1</FormReport>\n" +
            "                                        <CustomInspector>\n" +
            "                                            <ns17:PersonSurname>Филинкова</ns17:PersonSurname>\n" +
            "                                            <ns17:PersonName>С. Г.</ns17:PersonName>\n" +
            "                                            <ns17:PersonPost>Специалист по таможенной отчетности</ns17:PersonPost>\n" +
            "                                        </CustomInspector>\n" +
            "                                        <RegisterNumberReport>\n" +
            "                                            <cat_ru:CustomsCode>10702030</cat_ru:CustomsCode>\n" +
            "                                            <cat_ru:RegistrationDate>2019-02-04</cat_ru:RegistrationDate>\n" +
            "                                            <cat_ru:GTDNumber>0018553</cat_ru:GTDNumber>\n" +
            "                                        </RegisterNumberReport>\n" +
            "                                    </ns0:DORegInfo>\n" +
            "                                </Object>\n" +
            "                            </Signature>\n" +
            "                        </DocBody>\n" +
            "                    </ContainerDoc>\n" +
            "                </ED_Container>\n" +
            "            </Object>\n" +
            "        </Signature>\n" +
            "    </Body>\n" +
            "</Envelope>\n";
    private static final Integer OR_SEND_SUCCESS = 4;
    private static final Integer OR_SEND_ERROR = 5;
    private static final Integer OR_RECEIVE_SUCCESS = 6;
    private static final Integer OR_RECEIVE_ERROR = 7;
    private static final Integer OR_DELETE_SUCCESS = 8;
    private static final Integer OR_DELETE_ERROR = 9;

    @PostMapping("/test")
    @ResponseBody
    public String test(){
        return "hi";
    }
    
    @RequestMapping(value = "/sendFile.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public OperationResult sendFile(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name = "login", required = true) String login,
            @RequestParam(name = "password", required = true) String password,
            @RequestParam(name = "toLogin", required = true) String toLogin,
            @RequestParam(name = "msgtype", required = false) String msgType,
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "xmlFile", required = true) MultipartFile xmlFile,
            @RequestParam(name = "noDecode", required = false) String noDecode,
            @RequestParam(name = "compression", required = false) String compression,
            @RequestParam(name = "CompName", required = false) String compName,
            @RequestParam(name = "servId", required = true) String servId,
            @RequestParam(name = "clientProgram", required = false) String clientProgram,
            @RequestParam(name = "clientVersion", required = false) String clientVersion,
            @RequestParam(name = "clientSerial", required = false) String clientSerial
    ) throws Exception {
//        System.out.println("rec:"+ new String(xmlFile.getBytes()));

        Random rnd = new Random();
        
        OperationResult result = new OperationResult();
        if (rnd.nextInt(2) == 0) {
            java.io.File file = Paths.get("./zip/file.zip").toFile();
            if (!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(xmlFile.getBytes());
            fileOutputStream.close();
            result.setState(OR_SEND_SUCCESS);
            result.setMessage("send success");
        } else {
            result.setState(OR_SEND_ERROR);
            result.setMessage("send error");
        }
        
        return result;
    }
    
    @RequestMapping(value = "/getFiles.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public XMLFile getFiles(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name = "login", required = true) String login,
            @RequestParam(name = "password", required = true) String password,
            @RequestParam(name = "compName", required = false) String compName,
            @RequestParam(name = "servId", required = true) String servId
    ) throws Exception {

//        Thread.sleep(7000);
        Random rnd = new Random();
        
        XMLFile result = new XMLFile();
        result.setOperationResult(new XMLFile.OperationResult());
//        if (rnd.nextInt(2) == 0) {
//            result.getOperationResult().setMessageEx("Test");
//            result.getOperationResult().setIgnores("Test");
//            result.getOperationResult().setMessage("Получено успешно");
//            result.getOperationResult().setState(OR_RECEIVE_SUCCESS);
//            return result;
//        }
        if (rnd.nextInt(2) == 0) {
            result.getOperationResult().setState(OR_RECEIVE_SUCCESS);
            result.getOperationResult().setMessageEx("Test");
            result.getOperationResult().setIgnores("Test");
            result.getOperationResult().setMessage("Получено успешно");

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    GZIPOutputStream gos = new GZIPOutputStream(bos) ) {

                gos.write(TEST_FILE_CONTENT.getBytes());
                gos.close();

                File f = new File();
                f.setId(TEST_FILE_ID);
                f.setContent(bos.toByteArray());
                result.getFile().add(f);

                File f1 = new File();
                f1.setId("1");
                f1.setContent(bos.toByteArray());
                result.getFile().add(f1);

                File f2 = new File();
                f2.setId("2");
                f2.setContent(bos.toByteArray());
                result.getFile().add(f2);
            }
            
        } else {
            result.getOperationResult().setState(OR_RECEIVE_ERROR);
            result.getOperationResult().setMessage("Получено с ошибкой");
        }
        
        return result;
    }
    
    @RequestMapping(value = "/deleteFile.do", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public OperationResult deleteFile(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name = "login", required = true) String login,
            @RequestParam(name = "password", required = true) String password,
            @RequestParam(name = "fileId", required = false) String fileId
    ) throws Exception {
        Random rnd = new Random();
        
        OperationResult result = new OperationResult();
        if (rnd.nextInt(3) >= 1) {
            result.setState(OR_DELETE_SUCCESS);
            result.setMessage("Delete success");
        } else {
            result.setState(OR_DELETE_ERROR);
            result.setMessage("Delete error");
        }
        
        return result;
    }
}
