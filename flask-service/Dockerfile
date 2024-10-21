# Use a lightweight Python base image
FROM python:3.10-slim

# Set the working directory in the container
WORKDIR /app

# Install necessary system packages for OpenCV and other dependencies
RUN apt-get update && apt-get install -y \
    libgl1-mesa-glx \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Copy the requirements file into the container
COPY requirements.txt .

# Install the dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Ensure necessary directories are created
RUN mkdir -p /app/models/kgcn_model

# Copy the application code
COPY . .

# # Uncomment these lines if you need to manually copy pre-trained models
# COPY app/models/comment-validation-service/distilbert_semantic_classifier /app/models/comment-validation-service/distilbert_semantic_classifier
# COPY app/models/comment-validation-service/coral_comment_classifier /app/models/comment-validation-service/coral_comment_classifier
# COPY app/models/kgcn_model/* /app/models/kgcn_model/

# Expose the port that the Flask app will run on
EXPOSE 5000

# Set environment variables for model directories
ENV RATINGS_FILE_PATH=/app/ratings_final.txt
ENV KG_FILE_PATH=/app/kg_final.txt

# Command to start the Gunicorn server
CMD ["gunicorn", "-w", "1", "-b", "0.0.0.0:5000", "--timeout", "180", "wsgi:app"]

