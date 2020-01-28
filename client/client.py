import requests
import json
import pathlib
from datetime import datetime
import logging
import os
import subprocess
from time import sleep
import random

"""
Client to test cloud storage implementation
"""

__author__ = 'Ivan Varabyeu'
# master_url = 'http://master:5000/'
master_url = 'http://77.80.1.219:8080/'
min_node_count = 3
min_delete_count = 3
files = os.listdir('dogs')


def main():
    logging.basicConfig(filename='client.log',
                        level=logging.DEBUG,
                        format='%(asctime)s %(message)s')

    print('Start test script')
    logging.info('Start test script')

    # deploy_app()
    insert_data()
    # read_data()
    # delete_data()
    # range_query()

    logging.info('Stopped test script')
    print('Stopped test script')

    print('See client.log for details')


def deploy_app():
    print('start deploying app')
    logging.info('start deploying app')
    # subprocess.run(['sudo', 'docker-compose', 'up'])
    # output=subprocess.check_output("docker ps | wc -l", shell=True)
    sleep(5)
    print('wait')
    sleep(5)
    print('wait')
    sleep(5)
    # check_health()


def check_health():
    urls = [master_url,
            'http://node1:5001/',
            'http://node2:5002/',
            'http://node3:5003/',
            'http://node4:5004/',]
    print('storage app deployed')
    logging.info('storage app deployed')


def insert_data():
    print('start inserting data')
    logging.info('start inserting data')
    for f in files:
        logging.debug('insert data: ' + f)
        payload = {'file': open('dogs/'+f, 'rb')}
        requests.post(master_url+'api/v1/insert', files=payload)
        break
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
        logging.debug('request file: ' + filename)
        res = requests.get(master_url+'api/v1/search/' + filename)
        # get node
        node = res.json()
        logging.debug('response with' + filename + ' from node: ' + node)
        if node not in nodes:
            num_accessed_nodes += 1
            logging.debug('number accessed nodes: ' + str(num_accessed_nodes))
            nodes.append(node)
    print('stop reading random data')
    logging.info('stop reading random data')


def delete_data():
    print('start deleting data')
    logging.info('start deleting data')
    for i in (0, min_delete_count):
        res = requests.delete(master_url+'api/v1/delete/' + files[i])
        # if ok - continue, else repeat
    print('stop deleting data')
    logging.info('stop deleting data')


def range_query():
    pass


if __name__ == "__main__":
    main()
