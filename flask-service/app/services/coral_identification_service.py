import numpy as np
import cv2
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing import image


class CoralIdentificationService:
    def __init__(self):
        self.model = load_model(
            'app/models/coral-growth-monitor-service/model_mobilenet_20.keras')
        self.class_indices = {
            0: 'Acropora',
            1: 'Acropora Genus',
            2: 'PocilloPora'
        }

    def predict(self, img_path):
        img = image.load_img(img_path, target_size=(224, 224))
        img_array = image.img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0)
        img_array /= 255.0

        predictions = self.model.predict(img_array)
        predicted_class_index = np.argmax(predictions, axis=1)[0]
        predicted_label = self.class_indices[predicted_class_index]

        return {
            "predicted_label": predicted_label
        }
