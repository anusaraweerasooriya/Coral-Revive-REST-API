from flask import Flask
from app.routes import init_routes
from app.eureka_client import register_with_eureka
from app.config_loader import load_remote_config


def create_app():
    app = Flask(__name__)

    config_url = "http://localhost:8888/config-server/configs/flask-service"
    remote_config = load_remote_config(config_url)
    app.config.update(remote_config)

    register_with_eureka()

    init_routes(app)
    return app
