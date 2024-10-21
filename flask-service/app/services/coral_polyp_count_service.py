import torch
from ultralytics import YOLO
from PIL import Image
import tempfile
import os


class CoralPolypCountService:
    def __init__(self):

        model_path = '/app/models/coral-growth-monitor-service/coral_polyp_model3.pt'
        self.model = YOLO(model_path)

    def predict_polyp_count(self, img_path):

        with tempfile.NamedTemporaryFile(delete=False, suffix=".jpg") as temp_file:
            img = Image.open(img_path)
            img.save(temp_file.name)
            temp_image_path = temp_file.name

        results = self.model.predict(source=temp_image_path, save=True)

        polyp_count = len(results[0].boxes)

        os.remove(temp_image_path)

        return polyp_count
