# burpBridge
python与Burp 通信Bridge将http body传入python脚本中做进一步处理（你可以进行签名校验也可以进行加密解密）最后传回burpsuite中。


## 使用方法
burpsuite 导入插件
在配置页面设置python路径，设置脚本的位置。

#### pyBridge脚本API说明
```

import //////////////////


def parse(request_text):
   ////////////


def unparse(request_line,headers,body):
    ////////////


def transform(request_line,headers,body):
    nonce = str(headers["X-KK-NONCE"]).encode("utf-8")
    xtimestap = str(headers["X-KK-TIMESTAMP"]).encode("utf-8")
    aPPSecretStr = b"sdlXM5uNbBiWbpjA"
    message = body.encode("utf-8")+ b"|"+ nonce + b"|" + xtimestap + b"|" + aPPSecretStr
    signature = hashlib.sha1(message)
    signature = signature.hexdigest()
    headers["X-KK-SIGN"] = signature
    # if headers
    return request_line,headers,body


try:
    a = sys.argv[1]
    resp = base64.b64decode(a)
    resp = str(resp, 'utf-8')

    request_line, headers, body = parse(resp)
    # do in user
    request_line, headers, body = transform(request_line, headers, body)
    ###
    body = body.strip()
    result = unparse(request_line, headers, body)
    print(result)
except Exception as e:
    traceback.print_exc()
```
parse函数是将http raw数据转换成python json数据对象，unparse函数是将python数据对象转换成http raw数据，这两个函数就不需要去动它（当然有时可能会有bug）
transform函数就是对http各种数据进行处理的方法啦，可以在这里自定义处理逻辑。
