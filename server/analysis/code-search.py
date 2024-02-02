from datetime import datetime
from airflow.models import Variable
from airflow.decorators import dag, task

@dag(start_date=datetime(2024, 1, 31), schedule_interval=None, catchup=False)
def gh_code_search_within_org():

    @task
    def make_list():
        keywords = Variable.get('gh-keyword')
        return keywords.split(',')

    @task
    def search_org_by_keyword(keyword: str):
        import json
        import urllib.request
        import urllib.parse
        from urllib.request import Request
        from neo4j import GraphDatabase
        upsert_stmt = "MERGE (o: Org {name: '%s'}) MERGE (r: Repo {name: '%s'}) MERGE (k: Keyword {name: '%s'}) MERGE (o)-[:CONTAINS]->(r)-[:FOUND]->(k)"
        password = Variable.get('neo4j-password')
        release = Variable.get('neo4j-release')
        org = Variable.get('gh-org')
        ghtoken = Variable.get('gh-token')
        url = "neo4j://%s:7687" % (release)
        driver = GraphDatabase.driver(url, auth=('neo4j', password))
        handler = lambda tx, repo : tx.run(upsert_stmt % (repo[1], repo[2], repo[0]))
        q = "org:%s %s" % (org, keyword)
        query = "https://api.github.com/search/code?q=%s" % (urllib.parse.quote(q))
        headers = {
            "Accept": "application/vnd.github+json",
            "Authorization": "Token " + ghtoken,
            "X-GitHub-Api-Version": "2022-11-28"
        }
        already = []
        repos = []
        r = Request(query, headers=headers)
        with urllib.request.urlopen(r) as response:
            results = json.loads(response.read())
            if 'items' in results:
                for item in results['items']:
                    if 'repository' in item:
                        r = item['repository']
                        if 'name' in r:
                            name = r['name']
                            if not name in already:
                                already.append(name)
                                repos.append((keyword, org, name))
        with driver.session() as session:
            for repo in repos:
                session.execute_write(handler, repo)
        driver.close()

    search_org_by_keyword.partial().expand(keyword=make_list())

gh_code_search_within_org()
