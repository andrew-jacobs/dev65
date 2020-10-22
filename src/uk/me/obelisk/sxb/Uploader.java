//==============================================================================
//                                                     
//     SSSSSSSSSSSSSSS XXXXXXX       XXXXXXXBBBBBBBBBBBBBBBBB   
//   SS:::::::::::::::SX:::::X       X:::::XB::::::::::::::::B  
//  S:::::SSSSSS::::::SX:::::X       X:::::XB::::::BBBBBB:::::B 
//  S:::::S     SSSSSSSX::::::X     X::::::XBB:::::B     B:::::B
//  S:::::S            XXX:::::X   X:::::XXX  B::::B     B:::::B
//  S:::::S               X:::::X X:::::X     B::::B     B:::::B
//   S::::SSSS             X:::::X:::::X      B::::BBBBBB:::::B 
//    SS::::::SSSSS         X:::::::::X       B:::::::::::::BB  
//      SSS::::::::SS       X:::::::::X       B::::BBBBBB:::::B 
//         SSSSSS::::S     X:::::X:::::X      B::::B     B:::::B
//              S:::::S   X:::::X X:::::X     B::::B     B:::::B
//              S:::::SXXX:::::X   X:::::XXX  B::::B     B:::::B
//  SSSSSSS     S:::::SX::::::X     X::::::XBB:::::BBBBBB::::::B
//  S::::::SSSSSS:::::SX:::::X       X:::::XB:::::::::::::::::B 
//  S:::::::::::::::SS X:::::X       X:::::XB::::::::::::::::B  
//   SSSSSSSSSSSSSSS   XXXXXXX       XXXXXXXBBBBBBBBBBBBBBBBB   
//
// A Command Line Utility for WDC SXB and MMC Development Boards
//------------------------------------------------------------------------------
// Copyright (C)2019-2020 Andrew Jacobs.
//
// This work is made available under the terms of the Creative Commons
// Attribution-ShareAlike 4.0 International license.Open the following URL to
// see the details.
//
// https://creativecommons.org/licenses/by-sa/4.0/
//
//==============================================================================
// Notes:
//
//------------------------------------------------------------------------------

package uk.me.obelisk.sxb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.fazecast.jSerialComm.SerialPort;

/**
 * A tool for uploading and executing programs on an SXB or MMC board.
 * 
 * @author Andrew Jacobs
 */
public class Uploader
{
	/**
	 * Program entry point.
	 * 
	 * @param args			The command line arguments.
	 */
	public static void main (String [] args)
	{
		new Uploader ().run (args);
	}
	
	// SXB/MMC protocol command codes
    private static final byte	CMD_SYNC        = 0;
    private static final byte	CMD_ECHO        = 1;
    private static final byte	CMD_WRITE_MEM   = 2;
    private static final byte	CMD_READ_MEM    = 3;
    private static final byte	CMD_GET_INFO    = 4;
    private static final byte	CMD_EXEC_DEBUG  = 5;
    private static final byte	CMD_EXEC_MEM    = 6;
    private static final byte	CMD_WRITE_FLASH = 7;
    private static final byte	CMD_READ_FLASH  = 8;
    private static final byte	CMD_CLEAR_FLASH = 9;
    private static final byte	CMD_CHECK_FLASH = 10;
    private static final byte	CMD_EXEC_FLASH  = 11;

    /**
     * The serial port being used.
     */
	private SerialPort	serial = null;
	
	/**
	 * The device description bytes.
	 */
	private byte []		info = new byte [29];
	
	/**
	 * Constructs an <CODE>Uploader</CODE>.
	 */
	private Uploader ()
	{ }
	
	/**
	 * Process the command line arguments and perform the requested actions.
	 * 
	 * @param args			The command line arguments.
	 */
	private void run (String [] args)
	{
		int index = 0;

        // Handle empty command and requests for help
        if ((args.length == 0) || args [index].toLowerCase ().equals ("-?")) {
            System.out.println ("Usage:\tsxb (-?|ports)");
            System.out.println ("\tsxb -port <port> command*");
            System.out.println ("Commands:");
            System.out.println ("\tinfo");
            System.out.println ("\tshow <address> <size>");
            System.out.println ("\tsave <address> <size> <S28 File>");
            System.out.println ("\tsavebin <address> <size> <WDC File>");
            System.out.println ("\tload <S28 File>");
            System.out.println ("\tloadbin <WDC File>");
            System.out.println ("\texec <address>");
            System.out.println ("\tterm");

            System.exit (0);
        }
        
        // Display a list of ports
        if (args [index].toLowerCase ().equals ("ports")) {
            for (SerialPort port : SerialPort.getCommPorts())
                System.out.println (port.getSystemPortName());
            System.exit (0);
        }

        // Handle the port argument
        if (args [index].toLowerCase ().equals ("-port")) {
            if (++index < args.length) {
                for (SerialPort port : SerialPort.getCommPorts ()) {
                    if (args [index].equals (port.getSystemPortName ())) {
                        serial = port;
                        serial.setBaudRate(115200);
                        serial.setNumDataBits(8);
                        serial.setNumStopBits(1);
                        serial.setParity(SerialPort.NO_PARITY);
                        serial.setFlowControl(SerialPort.FLOW_CONTROL_CTS_ENABLED|SerialPort.FLOW_CONTROL_RTS_ENABLED);
                        serial.setComPortTimeouts (SerialPort.TIMEOUT_READ_BLOCKING, 1000, 1000);
                        ++index;
                        break;
                    }
                }
                if (serial == null) error ("Invalid port name");
            }
            else
                error ("Missing argument for port name");
        }
        
        if (index < args.length) {
            // All other commands commands need a port
            if (serial == null) error ("No port defined");

        	if (!serial.openPort ()) {
        		error ("Failed to open serial port");
        		System.exit (1);
        	};

        	try {
	            // Reset the board
	            serial.setDTR ();
	            Thread.sleep (300);
	            serial.clearDTR ();
	            Thread.sleep (300);
	            serial.setDTR ();
	            Thread.sleep (300);
	
	            // Read the board info
	            command (CMD_GET_INFO);
	            serial.readBytes(info, 29);
	
	            // The process remaining commands
	            while (index < args.length) {
	                String command = args [index++];
	
	                if (command.equalsIgnoreCase("info")) {
	                    boardInfo ();
	                }
	                else if (command.equalsIgnoreCase ("show")) {
	                    if ((index + 1) > args.length) error ("Missing start address and size");
	
	                    int addr = convert (args [index++]);
	                    int size = convert (args [index++]);
	
	                    showMemory (addr, readMemory (addr, size));
	                }
	                else if (command.equalsIgnoreCase ("load")) {
	                    if (index >= args.length) error ("Missing S28 filename");
	
	                    try {
	                        loadRecords (new BufferedReader (new FileReader (args [index++])));
	                    }
	                    catch (Exception err) {
	                        error ("Failed to load S28 file");
	                    }
	                }
	                else if (command.equalsIgnoreCase ("save")) {
	                    if ((index + 2) >= args.length) error ("Missing start address and size");
	
	                    try {
	                        int addr = convert (args [index++]);
	                        int size = convert (args [index++]);
	
	                        saveRecords (addr, readMemory (addr, size), new BufferedWriter (new FileWriter (args [index++])));
	                    }
	                    catch (Exception err) {
	                        error ("Failed ot save S28 file");
	                    }
	                }
	                else if (command.equalsIgnoreCase ("loadbin"))
	                {
	                    if (index >= args.length) error("Missing S28 filename");
	
	                    try
	                    {
	                        loadBinary (new BufferedInputStream (new FileInputStream (args[index++])));
	                    }
	                    catch (Exception err)
	                    {
	                        error("Failed to load S28 file");
	                    }
	                }
	                else if (command.equalsIgnoreCase ("savebin"))
	                {
	                    if ((index + 2) >= args.length) error("Missing start address and size");
	
	                    try
	                    {
	                        int addr = convert(args[index++]);
	                        int size = convert(args[index++]);
	
	                        saveBinary (addr, readMemory(addr, size),
	                        		new BufferedOutputStream (new FileOutputStream (args[index++])));
	                    }
	                    catch (Exception err)
	                    {
	                        error("Failed to save S28 file");
	                    }
	                }
	                else if (command.equalsIgnoreCase ("exec")) {
	                    if (index >= args.length) error ("Missing execution address");
	
	                    int addr = convert (args [index++]);
	
	                    executeMemory (addr);
	                }
	                else if (command.equalsIgnoreCase ("term")) {
	                    new Terminal (serial);
	                }
	                else
	                    error ("Invalid command '" + command + "'");
	            }
        	}
        	catch (Exception err) {
        		error ("Unhandled exception");
        	}
        }
        else
            error ("No command specified. Use -help for command syntax");
	}

	/**
	 * Print an error message and then exit.
	 * 
	 * @param message		The error message.
	 */
    private void error (String message)
    {
        System.err.println ("Error: " + message);
        System.exit (1);
    }

    /**
     * Try to start a command generating an error if the board does not
     * respond.
     * 
     * @param command		The command code.
     */
    private void command (byte command)
    {
        byte []         prefix = { (byte) 0x55, (byte) 0xaa };
        byte []         buffer = new byte [1];

        serial.writeBytes (prefix, 2);
        if ((serial.readBytes (buffer, 1) != 1) || (buffer [0] != (byte) 0xcc))
            error ("No response from SXB -- Try pressing RESET");

        buffer [0] = (byte) command;
        serial.writeBytes (buffer, 1);
    }
    
    /**
     * Read size bytes from target address addr and return them in a byte
     * array.
     * 
     * @param addr			The target start address.
     * @param size			
	 * @return  A byte array filled with the memory contents.
	 */
    private byte [] readMemory (int addr, int size)
    {
        byte []         buffer = new byte [3];
        byte []         data = new byte [size];

        command (CMD_READ_MEM);
        buffer [0] = (byte) ((addr >>  0) & 0xff);
        buffer [1] = (byte) ((addr >>  8) & 0xff);
        buffer [2] = (byte) ((addr >> 16) & 0xff);
        serial.writeBytes (buffer, 3);
        buffer [0] = (byte) ((size >>  0) & 0xff);
        buffer [1] = (byte) ((size >>  8) & 0xff);
        serial.writeBytes (buffer, 2);

        serial.readBytes (data, size);
        return (data);
    }

    /**
     * Write the data held in the byte array data into the SXB's memory at
     * target address addr.
     * 
     * @param addr		The target start address.
     * @param data		A byte array containing the new memory content.
     */
    private void writeMemory (int addr, byte [] data)
    {
        byte []         buffer = new byte [3];

        command (CMD_WRITE_MEM);
        buffer [0] = (byte) ((addr >> 0) & 0xff);
        buffer [1] = (byte) ((addr >> 8) & 0xff);
        buffer [2] = (byte) ((addr >> 16) & 0xff);
        serial.writeBytes (buffer, 3);
        buffer [0] = (byte) ((data.length >> 0) & 0xff);
        buffer [1] = (byte) ((data.length >> 8) & 0xff);
        serial.writeBytes (buffer, 2);
        serial.writeBytes (data, data.length);
    }

    /**
     * Command the board to execute code starting at the indicated address.
     * 
     * @param addr		The address to start execution.
     */
    private void executeMemory (int addr)
    {
        if ((info [4] == 'X') && ((info [3] == 0) || (info [3] == 1)))
        {
            byte[] buffer = new byte[16];

            buffer[0] = buffer[1] = 0x00;  // A
            buffer[2] = buffer[3] = 0x00;  // X
            buffer[4] = buffer[5] = 0x00;  // Y

            buffer[6] = (byte)(addr >> 0);  // PC
            buffer[7] = (byte)(addr >> 8);
            buffer[8] = 0x00; // DP?
            buffer[9] = 0x00;
            buffer[10] = (byte) 0xff; // SP
            buffer[11] = 0x00;
            buffer[12] = 0x34; // P
            buffer[13] = 1;
            buffer[14] = 0; // PBR?
            buffer[15] = 0; // DBR?

            writeMemory(0x7e00, buffer);
            command (CMD_EXEC_DEBUG);
        }
        else
        {
            byte[] buffer = new byte[3];

            buffer[0] = (byte)(addr >> 0);
            buffer[1] = (byte)(addr >> 8);
            buffer[2] = (byte)(addr >> 16);

            command (CMD_EXEC_MEM);
            serial.writeBytes (buffer, 3);
        }
    }

    /**
     * Convert the value in str to a number. Allows $ or 0x prefixes for
     * hexadecimal numbers.
     * 
     * @param str			The value to convert
     * @return The numeric value.
     */
    private int convert (String str)
    {
        if (str.startsWith ("$"))
            return (Integer.parseInt (str.substring (1), 16));

        if (str.startsWith ("0x") || str.startsWith ("0X"))
            return (Integer.parseInt (str.substring (2), 16));

        return (Integer.parseInt (str));
    }

    /**
     * Display some information about the board.
     */
    private void boardInfo ()
    {
        // Offset:  W65C02  W65C165 W65C816
        // 00:      00      4d      00
        // 01:      7E      59      7E
        // 02:      00      4d      00
        // 03:      00      43      01          CPU Type
        // 04:      58      02      58          Board Type
        // 05:      00      00      00
        // 06:      7E      02      7E
        // 07:      00      00      00
        // 08:      00      00      00
        // 09:      80      F8      00
        // 10:      00      00      00
        // 11:      FA      FA      E4
        // 12:      7E      02      7E
        // 13:      00      00      00
        // 14:      00      00      00
        // 15:      7F      03      7F
        // 16:      00      00      00
        // 17:      FA      FA      E4
        // 18:      FF      FF      FF
        // 19:      00      00      00
        // 20:      00      00      00
        // 21:      7F      7F      7F
        // 22:      00      00      00
        // 23:      FF      FF      FF
        // 24:      7F      7F      7F
        // 25:      00      00      00
        // 26:      FF      90      FF
        // 27:      FF      7F      FF

        switch (info [3]) {
        case 0: {
                System.out.print ("W65C02");
                System.out.println ((info [4] == 'X') ? "SXB" : " Unknown Board");
                break;
            }
        case 1: {
        		System.out.print ("W65C816");
        		System.out.println ((info [4] == 'X') ? "SXB" : " Unknown Board");
                break;
            }
        case 0x41:
        case 0x42:
        case 0x43: {
        	System.out.print ("W65C165 Rev. " + (char) info [3]);
                if ((info [0] == 'M') && (info [1] == 'Y') && (info [2] == 'M'))
                	System.out.println (" MMC");
                else
                	System.out.println (" Unknown Board");
                break;
            }
        default: {
        		System.out.println ("Unknown Processor");
                break;
            }
        }
    }

    /**
     * Output the data in the array in hex.
     * 
     * @param addr			The memory address.
     * @param data			The data.
     */
    private void showMemory (int addr, byte [] data)
    {
        int     size = data.length;
        int     offset = 0;
        char [] buffer = new char [16];

        while (size > 0) {
            System.out.print (String.format("%.6x", addr));
            System.out.print (":");

            for (int loop = 0; loop < 16; ++loop) {
            	System.out.print (" ");
                buffer [loop] = '.';
                if (loop < size) {
                    byte val = data [offset++];
                    System.out.print (String.format("%.2x", val));
                    val &= 0x7f;
                    if ((0x20 <= val) && (val <= 0x7e))
                        buffer [loop] = (char) val;
                }
                else
                	System.out.print ("..");
            }

            System.out.print (" |");
            System.out.print (buffer);
            System.out.println ("|");

            addr += 16;
            size -= 16;
        }
    }

    /**
     * Write the data in the array in SREC format to the specified writer.
     * 
     * @param addr			The memory address
     * @param data			The data array
     * @param writer		The writer to output to.
     * @throws IOException	If any I/O problems are detected. 
     */
    private void saveRecords (int addr, byte [] data, BufferedWriter writer)
    	throws IOException
    {
        int     size = data.length;
        int     offset = 0;

        while (size > 0) {
            int     count = (size > 32) ? 32 : size;
            int     check = count + 4;

            writer.write ("S2");
            writer.write (String.format ("%.2x", check));
            writer.write (String.format ("%.6x", addr));

            check += (addr >>  0) & 0xff;
            check += (addr >>  8) & 0xff;
            check += (addr >> 16) & 0xff;

            for (int loop = 0; loop < count; ++loop) {
                byte value = data [offset++];
                writer.write (String.format ("%.2x", value));
                check += value;
            }

            check = 0xff - (check & 0xff);
            writer.write (String.format ("%.2x", check));
            writer.newLine ();
            
            addr += count;
            size -= count;
        }
        writer.close ();
    }

    /**
     * Read SREC format records from the indicated reader and writes the data
     * into the boards memory.
     * 
     * @param reader		The reader to get records from.
     * @throws IOException	If any I/O problems are detected. 
     */
    private void loadRecords (BufferedReader reader)
    	throws IOException
    {
        String          line;
        int             total = 0;

        while ((line = reader.readLine()) != null) {
            int             addr;
            int             count;
            byte []         data = null;

            if (line.startsWith ("S2")) {
                count = Integer.parseInt (line.substring (2, 2), 16) - 4;
                addr = Integer.parseInt (line.substring (4, 6), 16);

                if ((data == null) || (data.length != count))
                    data = new byte [count];

                for (int offset = 0; offset < count; ++offset)
                    data [offset] = (byte) Integer.parseInt (line.substring (10 + 2 * offset, 2), 16);

                writeMemory (addr, data);
                total += count;
            }
        }
        reader.close ();

        System.out.println ("Loaded " + total + " ($" + Integer.toHexString (total) + ") bytes");
    }

    /**
     * Loads WDCs binary object format into board memory.
     * 
     * @param stream		The stream to read from.
     * @throws IOException	If any I/O problems are detected. 
     */
    private void loadBinary (BufferedInputStream stream)
    	throws IOException
    {
        int total = 0;

        if (stream.read () == 'Z')
        {
            byte[] header = new byte[6];

            while (stream.read (header, 0, header.length) == header.length)
            {
                int addr = header[0] | (header[1] << 8) | (header[2] << 16);
                int size = header[3] | (header[4] << 8) | (header[5] << 16);
                byte[] data = new byte[size];

                if (stream.read (data, 0, size) == size) {
                    writeMemory(addr, data);
                    total += size;
                }
                else
                    error ("Failed reading binary data");
            }
            System.out.println ("Loaded " + total + " ($" + Integer.toHexString (total) + ") bytes");
        }
        else
            error ("Not a WDC format binary file");

        stream.close ();
    }

    /**
     * Saves the data as a WDC format binary object on the indicated stream.
     * 
     * @param addr			The memory address.
     * @param data			The data.
     * @param stream		The stream to write to.
     * @throws IOException	If any I/O problems are detected. 
     */
    private void saveBinary (int addr, byte[] data, BufferedOutputStream stream)
    	throws IOException
    {
        byte[] header = new byte [7];

        header[0] = (byte) 'Z';
        header[1] = (byte)(addr >> 0);
        header[2] = (byte)(addr >> 8);
        header[3] = (byte)(addr >> 16);
        header[4] = (byte)(data.length >> 0);
        header[5] = (byte)(data.length >> 8);
        header[6] = (byte)(data.length >> 16);

        stream.write (header, 0, header.length);
        stream.write(data, 0, data.length);
        stream.close();
    }
}