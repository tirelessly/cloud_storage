import requests
import logging
import os
import random

"""
Client to test cloud storage implementation
"""

__author__ = 'Ivan Varabyeu'
master_url = 'http://localhost:5000/'
min_node_count = 3
min_delete_count = 3
files = os.listdir('dogs')
range_from = '8003.jpg'
range_to = '8112.jpg'


def main():
    logging.basicConfig(filename='client.log',
                        level=logging.DEBUG,
                        format='%(asctime)s %(message)s')

    print('Start test script')
    logging.info('Start test script')

    check_health()
    insert_data()
    read_data()
    delete_data()
    range_query()

    logging.info('Stopped test script')
    print('Stopped test script')

    print('See client.log for details')


def check_health():
    print('check app health')
    logging.info('check app health')
    urls = ['http://localhost:5000/',
            'http://localhost:5001/',
            'http://localhost:5002/',
            'http://localhost:5003/',
            'http://localhost:5004/', ]
    for url in urls:
        while True:
            res = requests.get(url+'/api/v1/status')
            if res.status_code == 200:
                print('    Online: ' + url)
                logging.info('Online: ' + url)
                break

    print('storage app deployed')
    logging.info('storage app deployed')


def insert_data():
    print('start inserting data')
    logging.info('start inserting data')
    for f in files:
        logging.debug('insert data: ' + f)
        payload = {'file': open('dogs/'+f, 'rb')}
        requests.post(master_url+'api/v1/insert', files=payload)
    print('stop inserting data')
    logging.info('stop inserting data')


def read_data():
    print('start reading random data')
    logging.info('start reading random data')
    num_accessed_nodes = 0
    logging.debug('number accessed nodes: ' + str(num_accessed_nodes))
    nodes = []
    temp_files = files
    while num_accessed_nodes < min_node_count:
        size = len(temp_files)
        index = random.randint(0, size-1)
        filename = temp_files[index]
        temp_files.remove(filename)
        print('    read file: ' + filename)
        logging.debug('read file: ' + filename)
        res = requests.get(master_url+'api/v1/search/' + filename, stream=True)
        with open('read_data/'+filename, 'wb') as fd:
            for chunk in res.iter_content(chunk_size=128):
                fd.write(chunk)
        node = res.headers.get('url')
        print('    received:' + filename + ' from node:' + node)
        logging.info('received:' + filename + ' from node:' + node)
        if node not in nodes:
            num_accessed_nodes += 1
            logging.debug('number accessed nodes: ' + str(num_accessed_nodes))
            nodes.append(node)
    print('    number of accessed nodes: ' + str(num_accessed_nodes))
    logging.info('number of accessed nodes: ' + str(num_accessed_nodes))
    print('stop reading random data')
    logging.info('stop reading random data')


def delete_data():
    print('start deleting data')
    logging.info('start deleting data')
    for i in range (0, min_delete_count):
        res = requests.delete(master_url+'api/v1/delete/' + files[i])
        node = res.headers.get('url')
        print('    deleting file:' + files[i] + ' from: ' + node)
        logging.info('deleting file:' + files[i] + ' from: ' + node)
    print('stop deleting data')
    logging.info('stop deleting data')


def range_query():
    print('start range search')
    logging.info('start range search')
    print('    from: ' + range_from)
    logging.info('from: ' + range_from)
    print('    to: ' + range_to)
    logging.info('to: ' + range_to)

    res = requests.get(master_url + 'api/v1/range/' +
                       range_from + '/' + range_to)
    print(res.json())

    print('stop range search')
    logging.info('stop range search')


if __name__ == "__main__":
    main()
