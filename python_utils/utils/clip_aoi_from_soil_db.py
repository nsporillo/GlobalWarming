import json
import psycopg2


def get_aoi_as_tiff_from_db(config, env, aoi_json_bounds, get_aoi_tiff_from_california_soildata):
    conn_string = "host=%s dbname=%s user=%s port=%s password=%s" % (config.get(env, 'soildb_host'),
                    config.get(env, 'soildb_dbname'),
                    config.get(env, 'soildb_user'),
                    config.get(env, 'soildb_port'),
                    config.get(env, 'soildb_password'))
    conn = psycopg2.connect(conn_string)
    cursor = conn.cursor()
    cmd = """SELECT
    ST_AsTIFF(
     ST_Union(
      ST_Clip(
       california_soildata.rast,
       ST_SetSRID(
        ST_GeomFromGeoJSON('%(aoi_json_bounds)s')
         , ST_SRID(california_soildata.rast)
        )
       )
      )
     )
    FROM california_soildata
    WHERE
    ST_Intersects(
     ST_SetSRID(
      ST_GeomFromGeoJSON('%(aoi_json_bounds)s')
       , ST_SRID(california_soildata.rast)
      )
     ,california_soildata.rast
     );""" % {"aoi_json_bounds": json.dumps(aoi_json_bounds)}
    cursor.execute(cmd)
    rast = cursor.fetchone()

    with open(get_aoi_tiff_from_california_soildata, 'wb') as f_out:
        f_out.write((rast[0]))
    conn.close()


def pour_point_elev_val_from_california_soildata(conn, pour_point_json):
    cursor = conn.cursor()
    cmd = """SELECT
    ST_Value(
     california_soildata.rast,
     ST_SetSRID(
      ST_GeomFromGeoJSON('%(pour_point_json)s')
      , ST_SRID(california_soildata.rast)
      )
     )
    FROM california_soildata
    WHERE
    ST_Intersects(
     ST_SetSRID(
      ST_GeomFromGeoJSON('%(pour_point_json)s')
      , ST_SRID(california_soildata.rast)
      )
     ,california_soildata.rast
     )""" % {"pour_point_json": json.dumps(pour_point_json[0]['geometry'])}
    cursor.execute(cmd)
    point_elevation = cursor.fetchone()
    return int(point_elevation[0])

