/*
 * Copyright (c) 1997, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package jdk.javadoc.internal.doclets.formats.html;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import jdk.javadoc.internal.doclets.formats.html.markup.ContentBuilder;
import jdk.javadoc.internal.doclets.formats.html.markup.Entity;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlTree;
import jdk.javadoc.internal.doclets.formats.html.markup.StringContent;
import jdk.javadoc.internal.doclets.formats.html.markup.Table;
import jdk.javadoc.internal.doclets.formats.html.markup.TableHeader;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.MemberSummaryWriter;
import jdk.javadoc.internal.doclets.toolkit.PropertyWriter;

/**
 * Writes property documentation in HTML format.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class PropertyWriterImpl extends AbstractMemberWriter
    implements PropertyWriter, MemberSummaryWriter {

    public PropertyWriterImpl(SubWriterHolderWriter writer, TypeElement typeElement) {
        super(writer, typeElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Content getMemberSummaryHeader(TypeElement typeElement, Content memberSummaryTree) {
        memberSummaryTree.add(MarkerComments.START_OF_PROPERTY_SUMMARY);
        Content memberTree = new ContentBuilder();
        writer.addSummaryHeader(this, memberTree);
        return memberTree;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMemberTree(Content memberSummaryTree, Content memberTree) {
        writer.addMemberTree(HtmlStyle.propertySummary,
                SectionName.PROPERTY_SUMMARY, memberSummaryTree, memberTree);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Content getPropertyDetailsTreeHeader(Content memberDetailsTree) {
        memberDetailsTree.add(MarkerComments.START_OF_PROPERTY_DETAILS);
        Content propertyDetailsTree = new ContentBuilder();
        Content heading = HtmlTree.HEADING(Headings.TypeDeclaration.DETAILS_HEADING,
                contents.propertyDetailsLabel);
        propertyDetailsTree.add(heading);
        return propertyDetailsTree;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Content getPropertyDocTreeHeader(ExecutableElement property) {
        Content propertyDocTree = new ContentBuilder();
        Content heading = new HtmlTree(Headings.TypeDeclaration.MEMBER_HEADING,
                new StringContent(utils.getPropertyLabel(name(property))));
        propertyDocTree.add(heading);
        return HtmlTree.SECTION(HtmlStyle.detail, propertyDocTree)
                .setId(name(property));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Content getSignature(ExecutableElement property) {
        return new MemberSignature(property)
                .addType(utils.getReturnType(typeElement, property))
                .toContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDeprecated(ExecutableElement property, Content propertyDocTree) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addComments(ExecutableElement property, Content propertyDocTree) {
        TypeElement holder = (TypeElement)property.getEnclosingElement();
        if (!utils.getFullBody(property).isEmpty()) {
            if (holder.equals(typeElement) ||
                    (!utils.isPublic(holder) || utils.isLinkable(holder))) {
                writer.addInlineComment(property, propertyDocTree);
            } else {
                Content link =
                        writer.getDocLink(LinkInfoImpl.Kind.PROPERTY_COPY,
                        holder, property,
                        utils.isIncluded(holder)
                                ? holder.getSimpleName() : holder.getQualifiedName(),
                            false);
                Content codeLink = HtmlTree.CODE(link);
                Content descfrmLabel = HtmlTree.SPAN(HtmlStyle.descfrmTypeLabel,
                        utils.isClass(holder)
                                ? contents.descfrmClassLabel
                                : contents.descfrmInterfaceLabel);
                descfrmLabel.add(Entity.NO_BREAK_SPACE);
                descfrmLabel.add(codeLink);
                propertyDocTree.add(HtmlTree.DIV(HtmlStyle.block, descfrmLabel));
                writer.addInlineComment(property, propertyDocTree);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTags(ExecutableElement property, Content propertyDocTree) {
        writer.addTagsInfo(property, propertyDocTree);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Content getPropertyDetails(Content propertyDetailsTreeHeader, Content propertyDetailsTree) {
        Content propertyDetails = new ContentBuilder(propertyDetailsTreeHeader, propertyDetailsTree);
        return getMemberTree(HtmlTree.SECTION(HtmlStyle.propertyDetails, propertyDetails)
                .setId(SectionName.PROPERTY_DETAIL.getName()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Content getPropertyDoc(Content propertyDocTree) {
        return getMemberTree(propertyDocTree);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSummaryLabel(Content memberTree) {
        Content label = HtmlTree.HEADING(Headings.TypeDeclaration.SUMMARY_HEADING,
                contents.propertySummaryLabel);
        memberTree.add(label);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableHeader getSummaryTableHeader(Element member) {
        return new TableHeader(contents.typeLabel, contents.propertyLabel,
                contents.descriptionLabel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Table createSummaryTable() {
        return new Table(HtmlStyle.memberSummary)
                .setCaption(contents.properties)
                .setHeader(getSummaryTableHeader(typeElement))
                .setColumnStyles(HtmlStyle.colFirst, HtmlStyle.colSecond, HtmlStyle.colLast)
                .setRowScopeColumn(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addInheritedSummaryLabel(TypeElement typeElement, Content inheritedTree) {
        Content classLink = writer.getPreQualifiedClassLink(
                LinkInfoImpl.Kind.MEMBER, typeElement, false);
        Content label;
        if (options.summarizeOverriddenMethods) {
            label = new StringContent(utils.isClass(typeElement)
                    ? resources.getText("doclet.Properties_Declared_In_Class")
                    : resources.getText("doclet.Properties_Declared_In_Interface"));
        } else {
            label = new StringContent(utils.isClass(typeElement)
                    ? resources.getText("doclet.Properties_Inherited_From_Class")
                    : resources.getText("doclet.Properties_Inherited_From_Interface"));
        }
        HtmlTree labelHeading = HtmlTree.HEADING(Headings.TypeDeclaration.INHERITED_SUMMARY_HEADING,
                label);
        labelHeading.setId(SectionName.PROPERTIES_INHERITANCE.getName()
                + links.getName(configuration.getClassName(typeElement)));
        labelHeading.add(Entity.NO_BREAK_SPACE);
        labelHeading.add(classLink);
        inheritedTree.add(labelHeading);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addSummaryLink(LinkInfoImpl.Kind context, TypeElement typeElement, Element member,
            Content tdSummary) {
        Content memberLink = HtmlTree.SPAN(HtmlStyle.memberNameLink,
                writer.getDocLink(context, typeElement,
                member,
                utils.getPropertyLabel(name(member)),
                false,
                true));

        Content code = HtmlTree.CODE(memberLink);
        tdSummary.add(code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addInheritedSummaryLink(TypeElement typeElement, Element member, Content linksTree) {
        String mname = name(member);
        Content content = writer.getDocLink(LinkInfoImpl.Kind.MEMBER, typeElement, member,
                utils.isProperty(mname) ? utils.getPropertyName(mname) : mname,
                false, true);
        linksTree.add(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addSummaryType(Element member, Content tdSummaryType) {
        addModifierAndType(member, utils.getReturnType(typeElement, (ExecutableElement)member), tdSummaryType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Content getDeprecatedLink(Element member) {
        return writer.getDocLink(LinkInfoImpl.Kind.MEMBER, member,
                utils.getFullyQualifiedName(member));
    }

    @Override
    public Content getMemberTreeHeader(){
        return writer.getMemberTreeHeader();
    }
}
