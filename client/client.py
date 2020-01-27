import requests
import json
import pathlib
from datetime import datetime
import logging

"""
Client to test cloud storage implementation
"""

__author__ = 'Ivan Varabyeu'


def main():
    logging.basicConfig(filename='client.log',
                        level=logging.DEBUG,
                        format='%(asctime)s %(message)s')
    logging.info('Start test script')
    print('Test script started')

    print('1. Deploy cloud storage app')
    deploy_app()
    print('2. Insert data in storage')
    insert_data()
    print('3. Random read from storage')
    read_data()
    print('4. Delete 2 objects from storage')
    delete_data()
    print('5. Perform range query')
    range_query()

    logging.info('Test script stopped.')
    print('Start test script')


def check_health():
    pass


def deploy_app():
    pass


def insert_data():
    pass


def read_data():
    pass


def delete_data():
    pass


def range_query():
    pass


if __name__ == "__main__":
    main()
