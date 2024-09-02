import os
import numpy as np
import pandas as pd
from tensorflow.keras.models import load_model
import joblib

class WeatherPredictionService:
    def __init__(self):
        # Load the trained LSTM model
        model_path = os.path.join(os.getcwd(), 'app/models/scheduling-service/LSTM.keras')
        self.model = load_model(model_path)

        # Load the scaler used for normalizing the data
        scaler_path = os.path.join(os.getcwd(), 'app/models/scheduling-service/scaler.pkl')
        self.scaler = joblib.load(scaler_path)

        # Load the dataset
        data_file_path = os.path.join(os.getcwd(), 'app/datasets/scheduling-service/daily_average.csv')
        self.dataset = pd.read_csv(data_file_path)

        # Preprocess the data
        self.dataset['date'] = pd.to_datetime(self.dataset['date'])
        self.num_cols = ['temp', 'visibility', 'dew_point', 'pressure', 'humidity', 'wind_speed', 'wind_deg', 'rain_1h', 'clouds_all']
        self.dataset[self.num_cols] = self.scaler.transform(self.dataset[self.num_cols])

        # Add additional time features
        self.dataset['month'] = self.dataset['date'].dt.month
        self.dataset['day_of_month'] = self.dataset['date'].dt.day
        self.dataset['day_of_year'] = self.dataset['date'].dt.dayofyear
        self.dataset['day_of_week'] = self.dataset['date'].dt.dayofweek

        # Define the sequence length
        self.seq_length = 30

    def predict_future_weather(self, target_date):
        last_date = self.dataset['date'].max()
        last_index = self.dataset.index[self.dataset['date'] == last_date].tolist()[0]

        if last_index < self.seq_length - 1:
            print("Insufficient historical data to generate sequence")
            return None

        input_seq = self.dataset[self.num_cols].values[last_index + 1 - self.seq_length: last_index + 1]
        input_seq = np.expand_dims(input_seq, axis=0)

        target_date = pd.to_datetime(target_date)
        days_ahead = (target_date - last_date).days

        predicted_values = self.model.predict(input_seq, verbose=0)
        input_seq = np.concatenate((input_seq[:, 1:], np.expand_dims(predicted_values, axis=1)), axis=1)

        predicted_values = self.scaler.inverse_transform(predicted_values)[0]
        prediction_dict = dict(zip(self.num_cols, predicted_values))

        # Convert the prediction dictionary to Python-native types
        result = {
            "temp": prediction_dict.get('temp', 30).item(),
            "pressure": prediction_dict.get('pressure', 1012).item(),
            "humidity": prediction_dict.get('humidity', 86).item(),
            "windSpeed": prediction_dict.get('wind_speed', 40).item(),
            "rain1h": prediction_dict.get('rain_1h', 0).item(),
            "cloudsAll": prediction_dict.get('clouds_all', 40).item(),
            "weatherDescription": "Partly Cloudy"
        }
        
        return result