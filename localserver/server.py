# curl -X GET http://localhost:8080/{contestId}/{index}

from http.server import HTTPServer, BaseHTTPRequestHandler
import webbrowser

new = 2

class Server(BaseHTTPRequestHandler):
    def do_GET(self):
        url = "https://codeforces.com/problemset"
        webbrowser.open(url, new=new)

    def do_POST(self):
        contestId, index = self.path.split('/')[1:]
        print('contestId = {}, index = {}'.format(contestId, index))
        url = "https://codeforces.com/problemset/problem/{}/{}".format(contestId, index)
        webbrowser.open(url, new=new)

httpd = HTTPServer(('localhost', 8080), Server)
httpd.serve_forever()