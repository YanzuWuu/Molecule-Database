import pubchempy as pcp
import random
import argparse
from time import sleep
import os


def downloadfrompubchem(cid, output_dir):
    try:
        c = pcp.Compound.from_cid(cid)
        element = c.elements
        name = c.iupac_name
        name =''.join(name.split())

        if(len(name)>80):
            name = name[0:80]

        num_element = str(len(element))
        bonds = c.bonds
        output_path = os.path.join(output_dir +"/" + str(name)+'.txt')

        with open(output_dir +"/" + str(name)+'.txt', 'w') as f:
            f.write(name+ '\n')
            f.write(num_element + '\n')
            for i in range(len(element)):
                f.write(str(element[i]) + '\n')

            for ii in range(len(bonds)):
                if bonds[ii].order <= 4:
                    for _ in range(bonds[ii].order):
                        f.write(str(bonds[ii].aid1-1) + ' ' + str(bonds[ii].aid2-1) + '\n')
                else:
                    # represent special bonds as just a single bond
                    f.write(str(bonds[ii].aid1-1) + ' ' + str(bonds[ii].aid2-1) + '\n')
        print("Saved compound to {0}".format(os.path.join(output_dir, str(name)+".txt")))
    except pcp.PubChemPyError as e:
        print(e)
        raise e



def main():
    print("Optional arguments:\n"
          "-id ID          The PubChem ID of compound want to download\n"
          "-random RANDOM  The Random number of compound to download\n")

    parser = argparse.ArgumentParser(description="""Download Data from PubChem using PubChem ID.
                                     Download Limits:
                                     No more than 5 requests per second.
                                     No more than 400 requests per minute.
                                     No longer than 300 second running time per minute.""")

    parser.add_argument("-i", "--id", type=int, help='The PubChem ID of compound want to download', required=False)

    parser.add_argument("-r", "--random", type=int,
        help='The Random number of compound  to download. This will randomly generate PubChem IDs and download them.',
        required=False)

    parser.add_argument("-o", "--output",
                        help='Output directory path to save compounds',
                        required=False,
                        default="../DownloadData")

    args = parser.parse_args()

    if args.id is None and args.random is None:
        print("No arguments passed. Either --int or --random needs to be specified.")
        raise Exception
    elif args.id is not None:
        if args.id == 0:
            raise Exception("0 is not a valid PubChem ID")
        else:
            pubchem_id = args.id
            print('Downloading specific compound data from PubChem...')
            path = os.getcwd() + "/DownloadData"
            downloadfrompubchem(pubchem_id, path)
            print('Completed !')

    elif args.random is not None:
        if args.random == 0:
            raise Exception("0 is not a valid # of random")
        num_compound = args.random
        print('Downloading Data from PubChem....')
        for i in range(int(num_compound)):
            compound_id = random.randint(1, 100000)
            if args.output is not None and os.path.isdir(args.output):
                path = args.output
            else:
                path = os.getcwd() + "/DownloadData"
            downloadfrompubchem(compound_id, path)
            # sleep for 0.1 seconds after each download to ensure download limits are not exceeded
            sleep(0.1)
        print('Completed !')



if __name__ == "__main__":
    main()