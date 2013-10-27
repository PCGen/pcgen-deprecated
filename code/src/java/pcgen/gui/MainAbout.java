/*
 * MainAbout.java
 * Copyright 2001 (C) Tom Epperly <tomepperly@home.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 26, 2001, 10:47 PM
 *
 * Current Ver: $Revision$ <br>
 * Last Editor: $Author$ <br>
 * Last Edited: $Date$
 *
 */
package pcgen.gui;

import pcgen.core.Globals;
import pcgen.gui.utils.BrowserLauncher;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JLabelPane;
import pcgen.persistence.lst.SponsorLoader;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import gmgen.gui.GridBoxLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Create a simple panel to identify the program and those who contributed
 * to it.
 *
 * @author  Tom Epperly <tomepperly@home.com>
 * @version $Revision$
 * Modified 4/8/02 by W Robert Reed III (Mynex)
 * Adds List Monkeys Display area
 * Cleaned up naming schema
 */
final class MainAbout extends JPanel
{
	static final long serialVersionUID = -423796320641536943L;
	private JButton mailingList;
	private JButton wwwSite;
	private JLabel dateLabel;
	private JLabel emailLabel;
	private JLabel helperLabel;
	private JLabel leaderLabel;
	private JLabel versionLabel;
	private JLabel javaVersionLabel;
	private JLabel wwwLink;
	private JScrollPane license;
	private JTabbedPane mainPane;
	private JTabbedPane monkeyTabPane;
	private JTextArea LGPLArea;
	private JTextArea otherLibrariesField;
	private JTextField projectLead;
	private JTextField releaseDate;
	private JTextField version;
	private JTextField javaVersion;

	/** Creates new form MainAbout */
	MainAbout() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		mainPane = new JTabbedPane();
		mainPane.add(PropertyFactory.getString("in_abt_credits"), buildCreditsPanel()); //$NON-NLS-1$
		mainPane.add(PropertyFactory.getString("in_abt_libraries"), buildIncludesPanel()); //$NON-NLS-1$
		mainPane.add(PropertyFactory.getString("in_abt_license"), buildLicensePanel()); //$NON-NLS-1$
		mainPane.add(PropertyFactory.getString("in_abt_awards"), buildAwardsPanel()); //$NON-NLS-1$
		mainPane.add(PropertyFactory.getString("in_abt_sponsors"), buildSponsorsPanel()); //$NON-NLS-1$

		setLayout(new BorderLayout());

		add(mainPane, BorderLayout.CENTER);
		mainPane.setPreferredSize(new Dimension(640, 480));
	}

	/**
	 * Construct the credits panel. This panel shows basic details
	 * about PCGen and lists all involved in it's creation.
	 *
	 * @return The credits panel.
	 */
	private JPanel buildCreditsPanel()
	{
		JPanel aCreditsPanel = new JPanel();

		versionLabel = new JLabel();
		dateLabel = new JLabel();
		javaVersionLabel = new JLabel();
		leaderLabel = new JLabel();
		helperLabel = new JLabel();
		wwwLink = new JLabel();
		emailLabel = new JLabel();
		version = new JTextField();
		releaseDate = new JTextField();
		javaVersion = new JTextField();
		projectLead = new JTextField();
		wwwSite = new JButton();
		mailingList = new JButton();
		monkeyTabPane = new JTabbedPane();

		aCreditsPanel = new JPanel();
		aCreditsPanel.setLayout(new GridBagLayout());

		GridBagConstraints gridBagConstraints1;

		// Labels

		versionLabel.setText(PropertyFactory.getString("in_abt_version")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 0, GridBagConstraints.WEST);
		gridBagConstraints1.weightx = 0.2;
		aCreditsPanel.add(versionLabel, gridBagConstraints1);

		dateLabel.setText(PropertyFactory.getString("in_abt_release_date")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 1, GridBagConstraints.WEST);
		aCreditsPanel.add(dateLabel, gridBagConstraints1);

		javaVersionLabel.setText(PropertyFactory.getString("in_abt_java_version")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 2, GridBagConstraints.WEST);
		aCreditsPanel.add(javaVersionLabel, gridBagConstraints1);

		leaderLabel.setText(PropertyFactory.getString("in_abt_BD")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 3, GridBagConstraints.WEST);
		aCreditsPanel.add(leaderLabel, gridBagConstraints1);

		wwwLink.setText(PropertyFactory.getString("in_abt_web")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 4, GridBagConstraints.WEST);
		aCreditsPanel.add(wwwLink, gridBagConstraints1);

		emailLabel.setText(PropertyFactory.getString("in_abt_email")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 5, GridBagConstraints.WEST);
		aCreditsPanel.add(emailLabel, gridBagConstraints1);

		helperLabel.setText(PropertyFactory.getString("in_abt_monkeys")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 6,
			GridBagConstraints.NORTHWEST);
		aCreditsPanel.add(helperLabel, gridBagConstraints1);

		// Info

		version.setEditable(false);
		version.setText(PCGenProp.getVersionNumber());
		version.setBorder(null);
		version.setOpaque(false);

		gridBagConstraints1 = buildConstraints(1, 0, GridBagConstraints.WEST);
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.weightx = 1.0;
		aCreditsPanel.add(version, gridBagConstraints1);

		releaseDate.setEditable(false);
		releaseDate.setText(PCGenProp.getReleaseDate());
		releaseDate.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
		releaseDate.setOpaque(false);

		gridBagConstraints1 = buildConstraints(1, 1, GridBagConstraints.WEST);
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		aCreditsPanel.add(releaseDate, gridBagConstraints1);

		javaVersion.setEditable(false);
		javaVersion.setText(System.getProperty("java.vm.version") + " (" + System.getProperty("java.vm.vendor")+")");
		javaVersion.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
		javaVersion.setOpaque(false);

		gridBagConstraints1 = buildConstraints(1, 2, GridBagConstraints.WEST);
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		aCreditsPanel.add(javaVersion, gridBagConstraints1);

		projectLead.setEditable(false);
		projectLead.setText(PCGenProp.getHeadCodeMonkey());
		projectLead.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
		projectLead.setOpaque(false);

		gridBagConstraints1 = buildConstraints(1, 3, GridBagConstraints.WEST);
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		aCreditsPanel.add(projectLead, gridBagConstraints1);

		// Web site button
		wwwSite.setText(PCGenProp.getWWWHome());
		wwwSite.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					BrowserLauncher.openURL(wwwSite.getText());
				}
				catch (IOException ioe)
				{
					Logging.errorPrint(PropertyFactory
						.getString("in_abt_browser_err"), ioe); //$NON-NLS-1$
				}
			}
		});
		gridBagConstraints1 = buildConstraints(1, 4, GridBagConstraints.WEST);
		aCreditsPanel.add(wwwSite, gridBagConstraints1);

		// Mailing list button
		mailingList.setText(PCGenProp.getMailingList());
		mailingList.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					BrowserLauncher.openURL(mailingList.getText());
				}
				catch (IOException ioe)
				{
					Logging.errorPrint(PropertyFactory
						.getString("in_abt_browser_err"), ioe); //$NON-NLS-1$
				}
			}
		});
		gridBagConstraints1 = buildConstraints(1, 5, GridBagConstraints.WEST);
		aCreditsPanel.add(mailingList, gridBagConstraints1);

		// Monkey tabbed pane
		gridBagConstraints1 = buildConstraints(1, 6, GridBagConstraints.WEST);
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		aCreditsPanel.add(monkeyTabPane, gridBagConstraints1);

		monkeyTabPane
			.add(
				PropertyFactory.getString("in_abt_code_mky"), buildMonkeyList(PCGenProp.getCodeMonkeys())); //$NON-NLS-1$
		monkeyTabPane
			.add(
				PropertyFactory.getString("in_abt_list_mky"), buildMonkeyList(PCGenProp.getListMonkeys())); //$NON-NLS-1$
		monkeyTabPane
			.add(
				PropertyFactory.getString("in_abt_test_mky"), buildMonkeyList(PCGenProp.getTestMonkeys())); //$NON-NLS-1$
		monkeyTabPane
			.add(
				PropertyFactory.getString("in_abt_eng_mky"), buildMonkeyList(PCGenProp.getEngineeringMonkeys())); //$NON-NLS-1$

		monkeyTabPane.setToolTipTextAt(2, PropertyFactory
			.getString("in_abt_easter_egg")); // because there isn't one //$NON-NLS-1$

		return aCreditsPanel;
	}

	/**
	 * Build up a scrollable list of monkeys, given the monkey names.
	 * @param monkeys The names of the monkeys
	 * @return A JScrollPane to display the monkeys.
	 */
	private JScrollPane buildMonkeyList(String monkeys)
	{
		JTextArea textArea = new JTextArea();
		JScrollPane scroller = new JScrollPane();

		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setText(monkeys);
		scroller.setViewportView(textArea);
		textArea.setCaretPosition(0);

		return scroller;
	}

	/**
	 * Construct a GridBagConstraints record using defaults and
	 * some basic supplied details.
	 *
	 * @param xPos The column the field should appear in.
	 * @param yPos The row the field should appear in.
	 * @param anchor Where the field should be positioned.
	 * @return A GridBagConstraints object.
	 */
	private GridBagConstraints buildConstraints(int xPos, int yPos, int anchor)
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = xPos;
		constraints.gridy = yPos;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = anchor;
		constraints.insets = new Insets(5, 0, 5, 10);
		return constraints;
	}

	/**
	 * Construct the includes panel. This panel shows details
	 * and licencing statrements about any libraries distributed
	 * with PCGen.
	 *
	 * @return The includes panel.
	 */
	private JPanel buildIncludesPanel()
	{
		JPanel iPanel = new JPanel();

		otherLibrariesField = new JTextArea();

		iPanel.setLayout(new BorderLayout());

		String s = PropertyFactory.getString("in_abt_lib_apache"); //$NON-NLS-1$
		s += PropertyFactory.getString("in_abt_lib_jdom"); //$NON-NLS-1$
		s += PropertyFactory.getString("in_abt_lib_l2f"); //$NON-NLS-1$
		otherLibrariesField.setText(s);
		otherLibrariesField.setWrapStyleWord(true);
		otherLibrariesField.setLineWrap(true);
		otherLibrariesField.setEditable(false);
		otherLibrariesField.setBorder(BorderFactory
			.createBevelBorder(BevelBorder.LOWERED));

		iPanel.add(otherLibrariesField, BorderLayout.CENTER);

		return iPanel;
	}

	/**
	 * Construct the awards panel. This panel shows each award 
	 * the pcgen project has been awarded
	 *  
	 * @return The awards panel.
	 */
	private JPanel buildAwardsPanel()
	{
		JScrollPane sp = new JScrollPane();
		JPanel panel = new JPanel();

		JPanel aPanel = new JPanel();
		aPanel.setLayout(new GridBoxLayout(2, 2));
		aPanel.setBackground(Color.WHITE);

		URL url = getClass().getResource(IconUtilitities.RESOURCE_URL + "gold200x200-2005.gif");
		if (url != null)
		{
			JLabel e2005 = new JLabel(new ImageIcon(url));
			aPanel.add(e2005);

			JTextArea title = new JTextArea();
			title.setLineWrap(true);
			title.setWrapStyleWord(true);
			title.setText(PropertyFactory.getString("in_abt_awards_2005_ennie"));
			aPanel.add(title);
		}

		url = getClass().getResource(IconUtilitities.RESOURCE_URL + "bronze200x200-2003.gif");
		if (url != null)
		{
			JLabel e2003 = new JLabel(new ImageIcon(url));
			aPanel.add(e2003);

			JTextArea title = new JTextArea();
			title.setLineWrap(true);
			title.setWrapStyleWord(true);
			title.setText(PropertyFactory.getString("in_abt_awards_2003_ennie"));
			aPanel.add(title);
		}

		sp.setViewportView(aPanel);
		panel.add(sp, BorderLayout.CENTER);
		return panel;
	}

	private JPanel buildSponsorsPanel()
	{
		Border etched = null;
		TitledBorder title = BorderFactory.createTitledBorder(etched, "Sponsor Info");
		title.setTitleJustification(TitledBorder.CENTER);
		JLabelPane sponsorLabel = new JLabelPane();
		JScrollPane sp = new JScrollPane(sponsorLabel);
		sp.setBorder(title);
		JPanel panel = new JPanel(new BorderLayout());
		sponsorLabel.setBackground(panel.getBackground());
		panel.add(sp, BorderLayout.CENTER);
		
		List<Map<String, String>> sponsors = Globals.getSponsors();
		StringBuffer sb = new StringBuffer();
		sb.append("<html><b>Our Sponsors</b><br>");
		for(int i = 0; i < sponsors.size(); i++) {
			Map<String, String> sponsor = sponsors.get(i);
			if(sponsor.get("SPONSOR").equals("PCGEN")) {
				continue;
			}
			
			sb.append("<img src='")
				.append(SponsorLoader.getConvertedSponsorPath(sponsor.get("IMAGEBANNER")))
				.append("'><br>");
		}
		sb.append("</html>");
		sponsorLabel.setText(sb.toString());
		return panel;
	}
	

	/**
	 * Construct the license panel. This panel shows the full
	 * text of the license under which PCGen is distributed.
	 *
	 * @return The license panel.
	 */
	private JPanel buildLicensePanel()
	{
		JPanel lPanel = new JPanel();

		license = new JScrollPane();
		LGPLArea = new JTextArea();

		lPanel.setLayout(new BorderLayout());

		LGPLArea.setEditable(false);

		InputStream lgpl = ClassLoader.getSystemResourceAsStream("LICENSE"); //$NON-NLS-1$

		if (lgpl != null)
		{
			try
			{
				LGPLArea.read(new InputStreamReader(lgpl), "LICENSE"); //$NON-NLS-1$
			}
			catch (IOException ioe)
			{
				LGPLArea.setText(PropertyFactory
					.getString("in_abt_license_read_err1")); //$NON-NLS-1$
			}
		}
		else
		{
			LGPLArea.setText(PropertyFactory
				.getString("in_abt_license_read_err2")); //$NON-NLS-1$
		}

		license.setViewportView(LGPLArea);
		lPanel.add(license, BorderLayout.CENTER);

		return lPanel;
	}
}