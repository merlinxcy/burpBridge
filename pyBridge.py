import hashlib
import hmac
import base64
import time
import sys
import re
import uuid
import urllib3
from io import StringIO
from http.client import HTTPResponse
import email
import traceback
import pprint
import io


def parse(request_text):
    '''
    https://stackoverflow.com/questions/4685217/parse-raw-http-headers
    :param request_text:
    :return:
    '''
    request_line, headers_alone = request_text.split('\r\n', 1)
    message = email.message_from_file(io.StringIO(headers_alone))
    headers = dict(message.items())
    body = re.findall(r"\r\n\r\n(.*)",headers_alone)[0]
    return request_line,headers,body


def unparse(request_line,headers,body):
    message = ""
    message = message + request_line + "\n"
    for i in headers:
        message = message + i + ": "+ headers[i]+ "\n"
    message = message + "\n" + body
    return message


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


# user()

# print("123123")
# print(sys.argv[1])
# a="UE9TVCAvYXBpL2NsaWVudC9hZ2VudC9tZW1iZXIvdjEvbGlzdCBIVFRQLzEuMQ0KSG9zdDogYWdlbnQueWlibzEwNjUuY29tDQpDb25uZWN0aW9uOiBjbG9zZQ0KQ29udGVudC1MZW5ndGg6IDE1Ng0KWC1LSy1BUFBLRVk6IDJaWUFxM0FWWkwNClgtS0stVElNRVNUQU1QOiAxNTk2Njg1MDQ3DQpUQi1VVUlEOiAyOTA5Q0EzOC02OTJFLTQ5NDktQkNDOC0yNDcwN0IxRUFGRDkNClRCLUFHRU5ULVRPS0VOOiBETF8yZmZmYTlkYWZiNjk0NWQxYWFjMzMyNjY5OGNmYzA0Mw0KWC1LSy1OT05DRTogRmRUb0VMNEJXOUlrV0R1cA0KVEItVkVSU0lPTjogMC4xDQpVc2VyLUFnZW50OiBNb3ppbGxhLzUuMCAoV2luZG93cyBOVCAxMC4wOyBXaW42NDsgeDY0KSBBcHBsZVdlYktpdC81MzcuMzYgKEtIVE1MLCBsaWtlIEdlY2tvKSBDaHJvbWUvODQuMC40MTQ3LjEwNSBTYWZhcmkvNTM3LjM2DQpUQi1UT0tFTjogDQpDb250ZW50LVR5cGU6IGFwcGxpY2F0aW9uL2pzb24NClRCLUNMSUVOVC1UWVBFOiBhZ2VudF93ZWINClgtS0stU1Y6IDENClgtS0stU0lHTjogMjY1NGUwZmY1MWY0ZWE0ODc4NDIyMDc5NjU1YjkwMzFiNmFhYTkwMQ0KVEItU0lURS1JRDogNg0KQWNjZXB0OiAqLyoNCk9yaWdpbjogaHR0cHM6Ly9hZ2VudC55aWJvMTA2NS5jb20NClNlYy1GZXRjaC1TaXRlOiBzYW1lLW9yaWdpbg0KU2VjLUZldGNoLU1vZGU6IGNvcnMNClNlYy1GZXRjaC1EZXN0OiBlbXB0eQ0KUmVmZXJlcjogaHR0cHM6Ly9hZ2VudC55aWJvMTA2NS5jb20vYXBwL3VzZXJJbmZvDQpBY2NlcHQtRW5jb2Rpbmc6IGd6aXAsIGRlZmxhdGUNCkFjY2VwdC1MYW5ndWFnZTogZW4tVVMsZW47cT0wLjksemgtQ047cT0wLjgsemg7cT0wLjcNCkNvb2tpZTogc2VyPWEwMzsgc2VyPWEwMzsgd2FmX2Nvb2tpZT0yOGY0ZGFiMC05NTAwLTQ3NzFhZTY4ZDI1NTlkNmI0YmRkNThiMDBmMWUyMGM1MmMwYjsgemdfZGlkPSU3QiUyMmRpZCUyMiUzQSUyMCUyMjE3M2MxZDA5OWY0OTllLTAxNTVjNTVhZmY1NDA0LTMzMjM3NjUtMWZhNDAwLTE3M2MxZDA5OWY1YmIyJTIyJTdEOyBfX3V0bWE9MjYxOTE3MDg1LjExMDkyMjI1NS4xNTk2Njg0NTQwLjE1OTY2ODQ1NDAuMTU5NjY4NDU0MC4xOyBfX3V0bWM9MjYxOTE3MDg1OyBfX3V0bXo9MjYxOTE3MDg1LjE1OTY2ODQ1NDAuMS4xLnV0bWNzcj0oZGlyZWN0KXx1dG1jY249KGRpcmVjdCl8dXRtY21kPShub25lKTsgX191dG1iPTI2MTkxNzA4NS4yLjkuMTU5NjY4NDU0ODc3OTsgemdfYTA2ZTQzMmQ0MGU5NDIxYjg4OWZjYTQ4YzI3YzY1YTc9JTdCJTIyc2lkJTIyJTNBJTIwMTU5NjY4NDU0MDQwOCUyQyUyMnVwZGF0ZWQlMjIlM0ElMjAxNTk2Njg0NTY0MTUzJTJDJTIyaW5mbyUyMiUzQSUyMDE1OTY2ODQ1NDA0MTElMkMlMjJzdXBlclByb3BlcnR5JTIyJTNBJTIwJTIyJTdCJTdEJTIyJTJDJTIycGxhdGZvcm0lMjIlM0ElMjAlMjIlN0IlN0QlMjIlMkMlMjJ1dG0lMjIlM0ElMjAlMjIlN0IlN0QlMjIlMkMlMjJyZWZlcnJlckRvbWFpbiUyMiUzQSUyMCUyMiUyMiU3RA0KDQp7InBhZ2VOdW0iOjEsInBhZ2VTaXplIjoxNSwicmVnaXN0ZXJTb3J0IjoxLCJkcmF3U29ydCI6IiIsImRlcG9zaXRTb3J0IjoiIiwibGFzdExvZ2luVGltZVNvcnQiOiIiLCJuYW1lIjoiIiwic3RhcnREYXRlIjoiMjAyMC0wOC0wMSIsImVuZERhdGUiOiIyMDIwLTA4LTA2In0="
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
