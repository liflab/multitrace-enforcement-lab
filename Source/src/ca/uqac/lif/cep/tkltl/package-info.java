/*
    A benchmark for multi-trace runtime enforcement in BeepBeep 3
    Copyright (C) 2021 Laboratoire d'informatique formelle

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Implementation of the TK-LTL operators as BeepBeep processors. TK-LTL
 * is an extension of Linear Temporal Logic with operators for counting.
 * Its semantics is defined in:
 * <blockquote>
 * R. Khoury, S. Hallé. (2018). Tally Keeping-LTL: An LTL semantics for
 * quantitative evaluation of LTL specifications. In In L. Bouzar-Benlabiod,
 * F. Khendek, S. Rubin, eds., 6th Intl. Workshop on Formal
 * Methods Integration, IEEE, 495–502.
 * </blockquote> 
 */
package ca.uqac.lif.cep.tkltl;