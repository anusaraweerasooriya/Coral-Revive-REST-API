from app.routes.coral_growth_monitor_routes import coral_growth_monitor


def init_routes(app):
    app.register_blueprint(coral_growth_monitor,
                           url_prefix='/api/coral-growth-monitor')
