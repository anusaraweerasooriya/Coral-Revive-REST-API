from app.routes.comment_verification_routes import comment_classifier_bp
from app.routes.coral_growth_monitor_routes import coral_growth_monitor
from app.routes.post_classification_routes import post_classification_bp
from app.routes.recommendation_route import recommendation_service_bp
from app.routes.resource_estimation_routes import (
    amount_of_bounding_glue_estimation_bp, manpower_estimation_bp,
    number_of_boats_estimation_bp, number_of_diving_kits_estimation_bp,
    number_of_reef_segments_estimation_bp, oxygen_capacity_bp,
    reef_bowl_estimation_bp, task_manpower_estimation_bp,
    task_skill_matching_bp)
from app.routes.schedule_routes import schedule_routes
from app.routes.update_model_routes import training_bp
from app.routes.weather_routes import weather_routes



def init_routes(app):
    app.register_blueprint(coral_growth_monitor, url_prefix='/api/coral-growth-monitor')
    app.register_blueprint(weather_routes, url_prefix='/api/weather')
    app.register_blueprint(schedule_routes, url_prefix='/api/schedule')
    app.register_blueprint(reef_bowl_estimation_bp, url_prefix='/api/reef-bowl-estimation')
    app.register_blueprint(manpower_estimation_bp, url_prefix='/api/manpower-estimation')
    app.register_blueprint(number_of_boats_estimation_bp, url_prefix='/api/number-of-boats-estimation')
    app.register_blueprint(number_of_diving_kits_estimation_bp, url_prefix='/api/number-of-diving-kits-estimation')
    app.register_blueprint(number_of_reef_segments_estimation_bp, url_prefix='/api/number-of-reef-segments-estimation')
    app.register_blueprint(amount_of_bounding_glue_estimation_bp, url_prefix='/api/amount-of-bounding-glue-estimation')
    app.register_blueprint(task_manpower_estimation_bp, url_prefix='/api/task-manpower-estimation')
    app.register_blueprint(task_skill_matching_bp, url_prefix='/api/task-skill-matching')
    app.register_blueprint(oxygen_capacity_bp, url_prefix='/api/oxygen-capacity-estimation')
    app.register_blueprint(comment_classifier_bp, url_prefix='/api/comment-verification')
    app.register_blueprint(training_bp, url_prefix='/api/update')
    app.register_blueprint(recommendation_service_bp, url_prefix='/api/')
    app.register_blueprint(post_classification_bp, url_prefix='/api/')

