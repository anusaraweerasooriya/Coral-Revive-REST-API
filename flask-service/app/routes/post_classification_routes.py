from flask import Blueprint, request, jsonify
from app.services.post_classification_service import PostClassificationService

# Initialize the Flask blueprint for the route
post_classification_bp = Blueprint('post_classification_route', __name__)

# Initialize the BART classification service
classification_service = PostClassificationService()

# Define the route to classify text
@post_classification_bp.route('/classifyPost', methods=['POST'])
def classify_text():
    data = request.json
    post = data.get('post')
    
    if not post:
        return jsonify({"error": "No text provided"}), 400

    # Call the classification service to classify the text
    result = classification_service.classify_text(post)

    # Return the classification result as JSON
    return jsonify(result)
