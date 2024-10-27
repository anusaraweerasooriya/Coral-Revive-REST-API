from flask import Blueprint, jsonify, request
from app.services import initialize_services

services = initialize_services()
recommendation_service = services["recommendation_service"]

recommendation_service_bp = Blueprint('recommendation_service_bp', __name__)

@recommendation_service_bp.route('/recommend', methods=['POST'])
def recommend():
    user_index = request.json['user_index']  # Only need user_index now
    
    recommended_items = recommendation_service.get_recommended_items(user_index)
    
    return jsonify({'recommended_items': recommended_items.tolist()})
