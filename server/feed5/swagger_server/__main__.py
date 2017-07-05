#!/usr/bin/env python3

import connexion
import logging
from .encoder import JSONEncoder
from flask_sqlalchemy import SQLAlchemy

if __name__ == '__main__':
    app = connexion.App(__name__, specification_dir='./swagger/')
    app.app.config.from_object('swagger_server.config.Config')
    app.app.config.from_envvar('APP_CONFIG')
    db = SQLAlchemy()
    db.init_app(app.app)
    logging.basicConfig()
    logging.getLogger('sqlalchemy.engine').setLevel(logging.INFO)
    app.app.json_encoder = JSONEncoder
    app.add_api('swagger.yaml', arguments={'title': 'news feed api'})
    app.run(port=8080)
