from flask import Flask, request, jsonify
import joblib
import numpy as np

app = Flask(__name__)

# Load model
model = joblib.load("model.pkl")

@app.route('/')
def home():
    return "✅ AI Model API is running!"

@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.get_json()
        amount = float(data.get("amount", 0))  # Example feature
        prediction = model.predict(np.array([[amount]]))[0]
        label = "High Spending" if prediction == 1 else "Normal Spending"
        return jsonify({"prediction": int(prediction), "label": label})
    except Exception as e:
        return jsonify({"error": str(e)}), 400

if __name__ == '__main__':
    app.run(port=5000)
