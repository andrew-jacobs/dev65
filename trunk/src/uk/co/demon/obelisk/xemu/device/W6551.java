/*
 * Copyright (C),2014 Andrew John Jacobs.
 *
 * This program is provided free of charge for educational purposes
 *
 * Redistribution and use in binary form without modification, is permitted
 * provided that the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS 'AS IS' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package uk.co.demon.obelisk.xemu.device;

import uk.co.demon.obelisk.xemu.AddressRange;
import uk.co.demon.obelisk.xemu.Device;

/**
 * 
 * @author 	Andrew Jacobs
 * @version	$Id$
 */
public class W6551 extends AddressRange implements Device
{
	protected W6551 (int base)
	{
		super (base, 4);
		
		reset ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset ()
	{
		status  = 0x10;
		command = 0x00;
		control = 0x00;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read (int address)
	{
		switch ((address - base) & 0x03) {
		case 0: return (dataIn);
		case 1:	return (status);
		case 2: return (command);
		case 3: return (control);
		}
		return (-1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write (int address, int value)
	{
		switch ((address - base) & 0x03) {
		case 0: dataOut = (byte) value; break;
		case 1:	status &= 0xfb; command &= 0xe0; break;
		case 2: command = (byte) value; break;
		case 3:	control = (byte) value;	break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load (int address, int value)
	{
		// Ignore loads into device
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute (int cycles)
	{
		// TODO: emulate behaviour
	}
	
	private byte		dataIn;
	@SuppressWarnings("unused")
	private byte		dataOut;
	private byte		status;
	private byte		command;
	private byte		control;
	
	@SuppressWarnings("unused")
	private boolean		txEmpty;
	@SuppressWarnings("unused")
	private boolean		rxFull;
}