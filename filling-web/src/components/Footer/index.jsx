import { useIntl } from 'umi';
import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-layout';
export default () => {
  const intl = useIntl();
  const defaultMessage = intl.formatMessage({
    id: 'app.copyright.produced',
    defaultMessage: '数据, 简单',
  });
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'filling',
          title: 'filling',
          href: 'https://github.com/zihjiang/filling',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/zihjiang/filling',
          blankTarget: true,
        },
        {
          key: 'Ant Design',
          title: '设计 by Ant Design',
          href: 'https://ant.design',
          blankTarget: true,
        },
      ]}
    />
  );
};
