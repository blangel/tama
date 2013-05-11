Tama
======

Polls `craigslist` periodically, filtering results by criteria, batches and sends as an email.


Compilation
-----------

Dash uses [ply](http://github.com/blangel/ply) as its build tool, ensure you have it installed.

Usage
--------

Lenoir uses [DropWizard](http://dropwizard.codahale.com/) to run.
Configure `tama.yml` to setup the username, encrypted-password (use jasypt) and decryption key.

Ply Commands
------------

Besides standard `ply` commands here are additional commands particular to the `Tama` module

* __Local server__ - to run the services locally for testing simply execute the following `ply` command:

        $ ply tama

    This will run the services using the configuration file at [src/main/resources/etc/tama/tama.yml](tama/blob/master/src/main/resources/etc/tama/tama.yml).
