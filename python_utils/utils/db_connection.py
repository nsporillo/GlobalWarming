from psycopg2 import connect

def get_db_connection(env, config):
    conn_string = "host=%s dbname=%s user=%s port=%s password=%s" % (config.get(env, 'host'),
                    config.get(env, 'dbname'),
                    config.get(env, 'user'),
                    config.get(env, 'port'),
                    config.get(env, 'password'))
    return connect(conn_string)

def get_california_soil_db_connection(env, config):
    conn_string = "host=%s dbname=%s user=%s port=%s password=%s" % (config.get(env, 'soildb_host'),
                    config.get(env, 'soildb_dbname'),
                    config.get(env, 'soildb_user'),
                    config.get(env, 'soildb_port'),
                    config.get(env, 'soildb_password'))
    return connect(conn_string)
