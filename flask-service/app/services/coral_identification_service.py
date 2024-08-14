import numpy as np
from tensorflow.keras.models import load_model
from tensorflow.keras.preprocessing import image


class CoralIdentificationService:
    def __init__(self):
        self.model = load_model(
            'app/models/coral-growth-monitor-service/model_mobilenet_20.keras')

    def predict(self, img_path):
        img = image.load_img(img_path, target_size=(224, 224))
        img_array = image.img_to_array(img)
        img_array = np.expand_dims(img_array, axis=0)
        img_array /= 255.0

        predictions = self.model.predict(img_array)
        predicted_class = np.argmax(predictions, axis=1)

        return {"predicted_class": int(predicted_class[0])}
