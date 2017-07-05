import redis
import json
from flask import current_app

class CachingService:
    rc = None

    def cache(self):
        if self.rc is None:
            self.rc = redis.StrictRedis(host=current_app.config['CACHE_HOST'], port=current_app.config['CACHE_PORT'], db=0)
        return self.rc

    def get(self, key: str) -> dict:
        v = self.cache().get(key)
        retVal = None
        if v is not None:
            retVal = json.loads(v.decode("utf-8"))
        return retVal

    def set(self, key: str, value: dict):
        self.cache().set(key, json.dumps(value))
