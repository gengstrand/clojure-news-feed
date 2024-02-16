from githubapi import LanguageSearch
from neo4depsdb import LanguageDependencies, CodeDependency
from datetime import datetime
from airflow.models import Variable
from airflow.decorators import dag, task

@dag(start_date=datetime(2021, 12, 1), schedule_interval=None, catchup=False)
def gh_org_languages():

    @task
    def make_list(language: str):
        ls = LanguageSearch(language)
        rv = ls.pages()
        if rv > 10:
            rv = 10
        return [*range(1, rv)]

    @task
    def search_repo_by_language(language: str, page: int):
        password = Variable.get('neo4j-password')
        release = Variable.get('neo4j-release')
        ls = LanguageSearch(language)
        repos = []
        for fn in ls.results(page):
            np = fn.split('/')
            if len(np) > 1:
                repos.append(CodeDependency(np[0], np[1], '', language))
            else:
                print("full_name %s not valid" % (fn))
        dao = LanguageDependencies(release, password)
        dao.upsert(repos)
        dao.close()

    search_repo_by_language.partial(language = 'Scala').expand(page=make_list('Scala'))
    search_repo_by_language.partial(language = 'Clojure').expand(page=make_list('Clojure'))
    search_repo_by_language.partial(language = 'Kotlin').expand(page=make_list('Kotlin'))
    search_repo_by_language.partial(language = 'C#').expand(page=make_list('C#'))

gh_org_languages()
