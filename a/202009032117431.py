from collections.abc import Iterable
import pymssql
import requests
import datetime


url = "http://ytsafe.gicp.net:8083/api/ytsafe/monitor/putdata"


connection = pymssql.connect(server='222.240.193.158:20114', user='pglTest', password='keyan',
                             database='OmsPingGuoLv')
cursor = connection.cursor()


def getPointList():
    sql = " SELECT DISTINCT PointId FROM ClinometerData WHERE MonitoringTime > '2020-07-01 13:18:10.150' "
    cursor.execute(sql)
    poinIdList = []
    for r in cursor.fetchall():
        poinIdList.append(r[0])
    return poinIdList


def getData(poinIdList):
    sql = """SELECT p.SensorId, a.Distance, a.MonitoringTime    FROM ClinometerData a
    LEFT JOIN Point p ON p.id = a.PointId
    WHERE a.PointId = '{}' and a.MonitoringTime in (
            SELECT MAX(MonitoringTime) FROM ClinometerData WHERE PointId = '{}'
    )"""

    record = '<Record sbbm="{}" Data="{}" status="0" cjsj="{}" />'

    xmlList = []
    for pid in poinIdList:
        cursor.execute(sql.format(pid, pid))
        for r in cursor.fetchall():
            xmlList.append(record.format(
                r[0], r[1], r[2].strftime('%Y%m%d%H%M%S')))
    return xmlList


def setXML(recordList):
    now = datetime.datetime.now().strftime('%Y%m%d%H%M%S')
    count = len(recordList)

    data = ""
    for x in recordList:
        data += x + '\r\n'

    xml = '''<?xml version="1.0" encoding="gb2312"?>
    <Body>
        <Head type="REQUEST" fsfid="59071271X" jsfid="0000" Fssj ="{}" jls="{}" />
        <Data ywlx="Data" sjcssj="{}" sjjssj="{}" jls="{}">
            {}
        </Data>
    </Body>
    '''.format(now, count, now, now, count, data)
    return xml


plist = getPointList()

recordList = getData(plist)
xml = setXML(recordList)

print(xml)

rec = requests.post(url, data={'xmlData': xml})

print(rec.content)
