from flask import Blueprint, request, jsonify
from app.services.comment_classification_service import CommentClassificationService

# Initialize the blueprint and the service
comment_classifier_bp = Blueprint('comment_classifier', __name__)
comment_classifier_service = CommentClassificationService()

@comment_classifier_bp.route('/classify', methods=['POST'])
def classify():

    if not request.json or 'post' not in request.json or 'comment' not in request.json:
        return jsonify({"error": "Both 'post' and 'comment' fields are required"}), 400


    post = request.json['post']
    comment = request.json['comment']

   
    prediction = comment_classifier_service.classify_comment(post, comment)

    return jsonify({"classification": prediction})
