import json
import time
import urllib.request
import urllib.parse
from urllib.request import Request
from typing import Generator
from abc import ABC, abstractmethod

class GithubApi(ABC):
    """common parts to calling the github API"""
    def __init__(self, ghtoken: str):
        if ghtoken == '':
            self.headers = {
                "Accept": "application/vnd.github+json"
            }
        else:
            self.headers = {
                "Accept": "application/vnd.github+json",
                "Authorization": "Token " + ghtoken,
                "X-GitHub-Api-Version": "2022-11-28"
            }

    @abstractmethod
    def query(self, page: int) -> str:
        """method to override that returns the actual endpoint"""
        pass

    def search(self, page: int) -> dict:
        """here is where the actual HTTP request is made"""
        time.sleep(6)
        r = Request(self.query(page), headers=self.headers)
        with urllib.request.urlopen(r) as response:
            return json.loads(response.read())

    def pages(self) -> int:
        """calls the endpoint to get the total number of pages"""
        results = self.search(0)
        rv = 0
        if 'total_count' in results:
            tc = results['total_count']
            if tc > 0 and tc < 100:
                rv = 1
            elif tc >= 100:
                rv = tc // 100
                if tc % 100 > 0:
                    rv = rv + 1
        return rv

class CodeSearch(GithubApi):
    """github code search API"""
    def __init__(self, org: str, keyword: str, ghtoken: str):
        super().__init__(ghtoken)
        self.org = org
        self.keyword = keyword

    def query(self, page: int) -> str:
        """possibly paginated search of code within an organization"""
        q = "org:%s %s" % (self.org, self.keyword)
        p = "&page=%d" % (page) if page > 0 else ""
        return "https://api.github.com/search/code?q=%s%s" % (urllib.parse.quote(q), p)

    def repositories(self) -> Generator[str, None, None]:
        """the repositories containing code that matches the keyword"""
        for p in range(self.pages()):
            results = self.search(p + 1)
            if 'items' in results:
                for item in results['items']:
                    if 'repository' in item:
                        r = item['repository']
                        if 'name' in r:
                            yield r['name']

class LanguageSearch(GithubApi):
    """github API searching repos written in a particular programming language"""
    def __init__(self, language: str):
        super().__init__('')
        self.language = language

    def query(self, page: int) -> str:
        """possibly paginated repository search"""
        if page == 0:
            return "https://api.github.com/search/repositories?q=language:%s" % (self.language)
        else:
            return "https://api.github.com/search/repositories?q=language:%s&sort=modified&order=desc&per_page=100&page=%d" % (self.language, page)

    def results(self, page: int) -> Generator[str, None, None]:
        """full name of repositories written in the language being searched"""
        rv = self.search(page)
        if 'items' in rv:
            for item in rv['items']:
                if 'full_name' in item:
                    yield item['full_name']
