#!/bin/bash

echo "Applying migrations..."
cd migrations
pwd
for file in *.sh
do
    echo "Applying migration $file"
    chmod u+x $file
    /bin/bash $file
    dateTime=`date '+%Y-%m-%d_%H-%M-%S'`
    mv $file ./applied_migrations/$dateTime$file
done
