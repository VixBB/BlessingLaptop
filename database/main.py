from fastapi import FastAPI, Header, HTTPException, Depends
from pydantic import BaseModel
import firebase_admin
from firebase_admin import credentials, auth
import os

app = FastAPI()

# ===== INIT FIREBASE =====
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
cred_path = os.path.join(BASE_DIR, "serviceAccountKey.json")

if not firebase_admin._apps:
    cred = credentials.Certificate(cred_path)
    firebase_admin.initialize_app(cred)

# ===== MODEL =====
class PinjamRequest(BaseModel):
    laptop_id: str

# ===== AUTH FUNCTION =====
def verify_token(id_token: str):
    decoded_token = auth.verify_id_token(id_token)
    return decoded_token["uid"]

def get_current_user(authorization: str = Header(...)):
    try:
        token = authorization.split(" ")[1]
        uid = verify_token(token)
        return uid
    except:
        raise HTTPException(status_code=401, detail="Invalid token")

# ===== ROUTES =====
@app.get("/")
def root():
    return {"message": "Server jalan 🔥"}

@app.post("/peminjaman")
def pinjam_laptop(
    request: PinjamRequest,
    uid: str = Depends(get_current_user)
):
    return {
        "message": f"Laptop {request.laptop_id} berhasil dipinjam oleh {uid}"
    }