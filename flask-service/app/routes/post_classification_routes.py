from flask import Blueprint, request, jsonify
import requests

# Initialize the Flask blueprint for the route
post_classification_bp = Blueprint('post_classification_route', __name__)

# Define the URL for the Modelbit API
MODELBIT_URL = "https://seminihiranya.us-east-1.aws.modelbit.com/v1/classifier_function/13"

# Define the route to classify text
@post_classification_bp.route('/classifyPost', methods=['POST'])
def classify_text():
    # Directly access the incoming JSON
    data = request.json
    post = data.get('post')

    if not post:
        return jsonify({"error": "No text provided"}), 400

    # Prepare the payload for the Modelbit API
    modelbit_data = {
        "data": {
            "text": post
        }
    }

    # Call the Modelbit API
    response = requests.post(MODELBIT_URL, json=modelbit_data)

    if response.status_code == 200:
        # Get the response data from Modelbit
        modelbit_result = response.json()
        
        # Extract the top two labels and scores from the response
        top_two_labels = modelbit_result['data'].get('top_two_labels', [])
        top_two_scores = modelbit_result['data'].get('top_two_scores', [])
        
        # Format the final response
        final_response = {
            "text": post,  # Include the original text
            "top_two_labels": top_two_labels,
            "top_two_scores": top_two_scores
        }
        
        return jsonify(final_response)  # Return the formatted response
    else:
        return jsonify({"error": "Failed to classify text", "status_code": response.status_code, "message": response.text}), response.status_code
