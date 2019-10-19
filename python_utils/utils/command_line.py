from optparse import OptionParser

def read_arguments():
    parser = OptionParser()
    parser.add_option("-i", "--ifile", dest="input_file_path",
                      help="Input File")
    parser.add_option("-j", "--ifile1", dest="input_file_path2",
                      help="Input File2")
    parser.add_option("-o", "--ofile", dest="output_file_path",
                      help="Output File")
    parser.add_option("-c", "--cfile", dest="config_file_path",
                      help="Config File")

    (options, args) = parser.parse_args()
    return [options.input_file_path, options.input_file_path2, options.output_file_path, options.config_file_path]
