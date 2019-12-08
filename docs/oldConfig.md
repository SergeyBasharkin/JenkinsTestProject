# Загрузка старых конфигов

Необходимо показывать пользователю наличие старых конфигураций.
Рекомендуемы алгоритм:
1. Запрос на `GET /config/old/list`
2. Отображение списка найденный настроек
3. У каждого конфига 2 кнопки:
    1. Посмотреть конфиг. Запрос на `GET /config/old?path={path}`, 
    где {path} - это путь, полученный на первом шаге
    2. Загрузить конфиг. Запрос на `GET /config/old/load?path={path}`,
    где {path} - это путь, полученный на первом шаге
    
# Описание запросов
```http request
GET /config/old/list

[
    "C:\\Users\\Sergey\\AppData\\LocalLow\\Sun\\Java\\Deployment\\cache\\6.0\\muffin\\configuration.muf",
    "C:\\Users\\Sergey\\AppData\\LocalLow\\Sun\\Java\\Deployment\\cache\\6.0\\muffin\\configuration1.muf",
    "C:\\Windows\\SysWOW64\\config\\systemprofile\\.jaltahttp\\configXML_1.XML",
    "C:\\Windows\\SysWOW64\\config\\systemprofile\\.jaltahttp\\configuration.xml"
]

GET /config/old?path=C:\\Windows\\SysWOW64\\config\\systemprofile\\.jaltahttp\\configuration.xml

{
    "url": "https://svd.alta.ru",
    "serverUsername": "serveralta",
    "connectionTimeout": 30000,
    "socketTimeout": 30000,
    "encryptionType": "RSA",
    "serverMode": "TIMER",
    "pushServerUrl": null,
    "operationLogDays": 0,
    "proxy": {
        "active": false,
        "url": null,
        "port": 0,
        "login": null,
        "password": null,
        "authType": "Basic"
    },
    "accounts": [
        {
            "id": 0,
            "login": "432250-ca",
            "password": "qVZwUVcNS/f3dTZwrWMkyA==",
            "serverId": "f04e93d0-4c1f-4ba3-9085-de9753d41c71",
            "sendDir": "C:\\Program Files (x86)\\CTM\\MONITOR_ED\\AltaHTTP\\OUTBOX\\",
            "receiveDir": "C:\\CTM\\INBOX\\AltaHTTP\\",
            "requestInterval": 30000,
            "msgType": null
        }
    ]
}

GET /config/old/load?path=C:\\Windows\\SysWOW64\\config\\systemprofile\\.jaltahttp\\configuration.xml

```