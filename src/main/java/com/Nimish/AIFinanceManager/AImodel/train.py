import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
import joblib

# Sample dataset
data = pd.read_csv("C:/NPC/AI-Finance-Manager/src/main/java/com/Nimish/AIFinanceManager/AImodel/Data/Personal_Finance_Dataset.csv")

# Example preprocessing
data['label'] = data['Amount'].apply(lambda x: 1 if x > 2000 else 0)  # Example label
X = data[['Amount']]  # You can add more features later
y = data['label']

# Train model
model = RandomForestClassifier()
model.fit(X, y)

# Save model
joblib.dump(model, 'model.pkl')
print("✅ Model trained and saved")
