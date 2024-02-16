from githubapi import CodeSearch
from neo4depsdb import KeywordDependencies, CodeDependency
from datetime import datetime
from airflow.models import Variable
from airflow.decorators import dag, task
from airflow.utils.log.logging_mixin import LoggingMixin

@dag(start_date=datetime(2024, 1, 31), schedule_interval=None, catchup=False)
def gh_code_search_within_org():

    @task
    def make_list():
        keywords = Variable.get('gh-keyword')
        return keywords.split(',')

    @task
    def search_org_by_keyword(keyword: str):
        password = Variable.get('neo4j-password')
        release = Variable.get('neo4j-release')
        org = Variable.get('gh-org')
        ghtoken = Variable.get('gh-token')
        already = []
        repos = []
        cs = CodeSearch(org, keyword, ghtoken)
        LoggingMixin().log.info(cs.query(1))
        for repo in cs.repositories():
            if not repo in already:
                already.append(repo)
                repos.append(CodeDependency(org, repo, keyword))
        dao = KeywordDependencies(release, password)
        dao.upsert(repos)
        dao.close()

    search_org_by_keyword.partial().expand(keyword=make_list())

gh_code_search_within_org()
