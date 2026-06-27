import psycopg2
import os

# Datos de conexion a tu PostgreSQL
# Cambia estos valores por los tuyos de PgAdmin
DB_CONFIG = {
    "host":     "aws-1-us-east-1.pooler.supabase.com",
    "port":     "5432",
    "database": "postgres",
    "user":     "postgres.lqzgedqjntomzubacvqi",
    "password": "TF8D111CD207C",
    "sslmode":  "require",
}

def get_connection():
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        return conn
    except Exception as e:
        print(f"Error conectando a la BD: {e}")
        raise

def test_connection():
    """Prueba que la conexion funciona"""
    try:
        conn = get_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT version();")
        version = cursor.fetchone()
        print(f"Conexion exitosa: {version[0]}")
        cursor.close()
        conn.close()
        return True
    except Exception as e:
        print(f"Fallo la conexion: {e}")
        return False

# Ejecuta esto para probar: python database.py
if __name__ == "__main__":
    test_connection()