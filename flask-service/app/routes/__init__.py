from app.routes.coral_growth_monitor_routes import coral_growth_monitor
from app.routes.weather_routes import weather_routes
from app.routes.schedule_routes import schedule_routes


def init_routes(app):
    app.register_blueprint(coral_growth_monitor,
                           url_prefix='/api/coral-growth-monitor')
    app.register_blueprint(weather_routes, url_prefix='/api/weather')
    app.register_blueprint(schedule_routes, url_prefix='/api/schedule')
