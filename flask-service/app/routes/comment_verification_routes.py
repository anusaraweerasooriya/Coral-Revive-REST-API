import requests
from flask import Blueprint, request, jsonify

# Initialize the blueprint
comment_classifier_bp = Blueprint('comment_classifier', __name__)

# URL of your Modelbit classifier
MODELBIT_URL = "https://seminihiranya.us-east-1.aws.modelbit.com/v1/classifier_function/10"

@comment_classifier_bp.route('/classify', methods=['POST'])
def classify():
    # Check if the request contains JSON and the required fields
    if not request.json or 'post' not in request.json or 'comment' not in request.json:
        return jsonify({"error": "Both 'post' and 'comment' fields are required"}), 400

    # Access the post and comment directly from the request
    post = request.json['post']
    comment = request.json['comment']

    # Prepare the request payload for Modelbit
    modelbit_data = {
        "data": {
            "post": post,
            "comment": comment
        }
    }

    # Send the request to Modelbit
    response = requests.post(MODELBIT_URL, json=modelbit_data)

    if response.status_code == 200:
        classification_result = response.json()
        print("Modelbit Response:", classification_result)  # Log the response
        
        # Access the classification from the 'data' field
        classification = classification_result['data'].get('classification', 'unknown')  # Default to 'unknown'
        
        # Return only the classification result as specified
        return jsonify({"classification": classification})
    else:
        return jsonify({"error": "Failed to classify comment", "status_code": response.status_code, "message": response.text}), response.status_code
