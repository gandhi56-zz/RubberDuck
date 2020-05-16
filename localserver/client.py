import httplib, subprocess

c = httplib.HTTPConnection('localhost', 8080)
c.request('POST', '/foo', '{}')
doc = c.getresponse().read()
print doc
