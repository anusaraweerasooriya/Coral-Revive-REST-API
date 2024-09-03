from app.routes.coral_growth_monitor_routes import coral_growth_monitor
from app.routes.comment_verification_routes import comment_classifier_bp
from app.routes.update_model_routes import training_bp
from app.routes.recommendation_route import recommendation_service_bp

def init_routes(app):
    app.register_blueprint(coral_growth_monitor,
                           url_prefix='/api/coral-growth-monitor')
    app.register_blueprint(comment_classifier_bp,
                           url_prefix='/api/comment-verification')
    app.register_blueprint(training_bp,
                           url_prefix='/api/update')
    app.register_blueprint(recommendation_service_bp,
                           url_prefix='/api/')
    
