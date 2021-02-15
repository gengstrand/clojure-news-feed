#!/usr/bin/env python3

import connexion
import logging
from .encoder import JSONEncoder
from flask_sqlalchemy import SQLAlchemy
from flask_cors import CORS

def start():
    app = connexion.App(__name__, specification_dir='./swagger/')
    app.app.config.from_object('swagger_server.config.Config')
    app.app.config.from_envvar('APP_CONFIG')
    db = SQLAlchemy()
    db.init_app(app.app)
    logging.basicConfig()
    logging.getLogger('sqlalchemy.engine').setLevel(logging.WARN)
    app.app.json_encoder = JSONEncoder
    app.add_api('swagger.yaml', arguments={'title': 'news feed api'})
    CORS(app.app)
    return app

def application():
    return start().app

if __name__ == '__main__':
    start().run(port=8080, threaded=True)
